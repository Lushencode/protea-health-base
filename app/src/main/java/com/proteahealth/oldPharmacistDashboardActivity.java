package com.proteahealth;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.util.AtomicFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;

import static com.proteahealth.oldPharmacistState.*;

/** Pharmacist workbench. Persistent LOCAL demo until team APIs are supplied. */
public class oldPharmacistDashboardActivity extends Activity {
    private oldPharmacistState state;
    private LinearLayout page;
    private AtomicFile file;
    private static final ExecutorService io=Executors.newSingleThreadExecutor();
    private String owner, selectedOrder="", tab="Orders", query="", filter="All";
    private boolean busy;
    protected boolean demoPreview() { return false; }
    private String sessionOwner() {
        android.content.SharedPreferences p=getSharedPreferences("ProteaHealthSession",MODE_PRIVATE);
        String role=p.getString("user_role","");
        String id=p.getString("user_id","");
        if(demoPreview()) return "explicit-demo";
        if(!p.getBoolean("is_logged_in",false) || id.isEmpty() || !(role.equalsIgnoreCase("pharmacy") || role.equalsIgnoreCase("pharmacist"))) return "";
        return "provider:"+id;
    }
    @Override protected void onCreate(Bundle saved) {
        setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        super.onCreate(saved);
        owner=sessionOwner();
        if(owner.isEmpty()) { showLoginRequired(); return; }
        if(saved!=null) { tab=saved.getString("tab","Orders"); selectedOrder=saved.getString("order",""); query=saved.getString("query",""); filter=saved.getString("filter","All"); }
        try {
            byte[] hash=MessageDigest.getInstance("SHA-256").digest(owner.getBytes(StandardCharsets.UTF_8));
            StringBuilder name=new StringBuilder(); for(byte b:hash) name.append(String.format(Locale.ROOT,"%02x",b));
            File dir=new File(getFilesDir(),"pharmacist_demo"); if(!dir.exists() && !dir.mkdirs()) throw new IOException("Storage unavailable");
            file=new AtomicFile(new File(dir,name+".bin"));
        } catch(Exception e) { failure("Cannot access local storage"); return; }
        textScreen("Loading pharmacist workbench…");
        io.execute(() -> {
            try {
                oldPharmacistState loaded;
                if(file.getBaseFile().exists()) {
                    try(ObjectInputStream input=new ObjectInputStream(file.openRead())) { loaded=(oldPharmacistState)input.readObject(); }
                } else { loaded= oldPharmacistState.sample(); write(loaded); }
                oldPharmacistState result=loaded;
                runOnUiThread(() -> { if(isFinishing() || isDestroyed()) return; state=result; render(); });
            } catch(Exception e) { runOnUiThread(() -> failure("Could not load local data. No records were overwritten.")); }
        });
    }
    @Override protected void onResume() {
        super.onResume();
        if(owner!=null && !owner.equals(sessionOwner())) { state=null; showLoginRequired(); }
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out); out.putString("tab",tab); out.putString("order",selectedOrder); out.putString("query",query); out.putString("filter",filter);
    }
    private void write(oldPharmacistState data) throws IOException {
        FileOutputStream stream=null;
        try { stream=file.startWrite(); ObjectOutputStream out=new ObjectOutputStream(stream); out.writeObject(data); out.flush(); file.finishWrite(stream); }
        catch(IOException e) { if(stream!=null) file.failWrite(stream); throw e; }
    }
    interface Change { void apply(oldPharmacistState next); }
    private void change(Change action, String success) {
        if(busy || state==null || !owner.equals(sessionOwner())) return;
        oldPharmacistState next=state.copy();
        try { action.apply(next); } catch(Exception e) { toast(e.getMessage()); return; }
        busy=true; render();
        io.execute(() -> {
            try { write(next); runOnUiThread(() -> { if(isDestroyed() || isFinishing()) return; busy=false; state=next; render(); toast(success); }); }
            catch(Exception e) { runOnUiThread(() -> { if(isDestroyed()) return; busy=false; render(); toast("Save failed. Previous data retained; please retry."); }); }
        });
    }
    private void showLoginRequired() {
        textScreen("Sign in with your pharmacist account to open this dashboard.");
        button(page,"Go to login",() -> { startActivity(new Intent(this,LoginActivity.class)); finish(); });
    }
    private void failure(String message) { textScreen(message); button(page,"Close",this::finish); }
    private void textScreen(String message) { screen(false); text(page,message,18,true); }
    private void screen(boolean dark) {
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(18),dp(20),dp(18),dp(24));
        page.setBackgroundColor(dark?0xFF162D2B:0xFFF2F8F6); scroll.addView(page);
        scroll.setOnApplyWindowInsetsListener((v,insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom()); return insets;
        });
        setContentView(scroll); scroll.requestApplyInsets();
    }
    private void render() {
        if(state==null) return;
        screen(state.darkMode);
        LinearLayout heading=card(page,0xFF00695C);
        TextView title=text(heading,state.profile.name,23,true); title.setTextColor(Color.WHITE);
        TextView subtitle=text(heading,"Pharmacist workbench · LOCAL DEMO",14,true); subtitle.setTextColor(0xFFD5F2E7);
        text(page,"Fictional orders. Edits stay on this device; no patient requests, payments or notifications are sent to the server.",13,false).setTextColor(state.darkMode?Color.WHITE:0xFF435851);
        if(busy) { text(page,"Saving…",16,true); return; }
        HorizontalScrollView nav=new HorizontalScrollView(this); LinearLayout tabs=new LinearLayout(this); tabs.setOrientation(LinearLayout.HORIZONTAL); nav.addView(tabs); page.addView(nav);
        for(String name:new String[]{"Orders","Inventory","Profile","Demand","Outbox","Settings"}) button(tabs,(tab.equals(name)?"● ":"")+name,() -> { tab=name; selectedOrder=""; query=""; render(); });
        if(!selectedOrder.isEmpty()) { orderDetail(); return; }
        switch(tab) {
            case "Inventory": inventory(); break;
            case "Profile": profile(); break;
            case "Demand": demand(); break;
            case "Outbox": outbox(); break;
            case "Settings": settings(); break;
            default: orders();
        }
    }
    private void orders() {
        LinearLayout summary=card(page,0xFFE0F2EF);
        long active=state.orders.stream().filter(o -> o.status!=Status.COMPLETED && o.status!=Status.CANCELLED && o.status!=Status.REJECTED).count();
        text(summary,active+" active requests · "+state.orders.stream().filter(o->o.refill).count()+" refill requests",18,true);
        text(summary,"Verify → prepare → ready → collected / dispatched → delivered",13,false);
        EditText search=input(page,"Search patient, order or medication",query);
        Spinner options=new Spinner(this);
        String[] values={"All","Received","Verified","Preparing","Ready","Dispatched","Completed","Rejected","Cancelled","Delivery","Refills"};
        options.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,values));
        options.setSelection(Arrays.asList(values).indexOf(filter)<0?0:Arrays.asList(values).indexOf(filter)); page.addView(options);
        LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); page.addView(list);
        Runnable fill=() -> {
            list.removeAllViews();
            for(Order o:state.orders) {
                boolean match=filter.equals("All") || filter.equalsIgnoreCase(o.status.name()) || (filter.equals("Delivery")&&o.delivery) || (filter.equals("Refills")&&o.refill);
                String haystack=o.patient+o.id; for(Line l:o.lines) haystack+=" "+l.description;
                if(!match || !haystack.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) continue;
                LinearLayout row=card(list,o.status==Status.RECEIVED?0xFFFFF1DD:0xFFFFFFFF);
                text(row,o.id+" · "+o.patient,18,true);
                text(row,o.status+" · "+(o.delivery?"Delivery":"Collection")+(o.refill?" · Refill":""),14,true);
                text(row,o.lines.size()+" medication(s) · "+money(o.total()),15,false);
                button(row,"Review request / order details",() -> { selectedOrder=o.id; render(); });
            }
            if(list.getChildCount()==0) text(list,"No requests match these filters.",16,false);
        };
        search.addTextChangedListener(watch(s -> { query=s; fill.run(); }));
        options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> p) {}
            public void onItemSelected(AdapterView<?> p,View v,int i,long id) { filter=values[i]; fill.run(); }
        }); fill.run();
    }
    private void orderDetail() {
        Order o=state.order(selectedOrder); String id=o.id;
        button(page,"← Back to orders",() -> { selectedOrder=""; tab="Orders"; render(); });
        LinearLayout details=card(page,0xFFFFFFFF);
        text(details,id+" · "+o.patient,21,true); text(details,"Status: "+o.status,16,true);
        text(details,"Contact: "+o.phone+"\n"+(o.delivery?"Delivery to: ":"Collection: ")+o.address+(o.refill?"\nRefill requested":""),15,false);
        for(Line l:o.lines) text(details,l.description+"\n"+l.quantity+" pack(s) × "+money(l.unitPrice),15,false);
        if(o.delivery) text(details,"Delivery fee: "+money(o.deliveryFee),15,false);
        text(details,"Order total: "+money(o.total()),19,true);
        LinearLayout rx=card(page,0xFFFCEAEF); text(rx,"Prescription review",19,true);
        text(rx,o.prescription,15,false);
        if(!o.attachmentUri.isEmpty()) {
            ImageView image=new ImageView(this); image.setAdjustViewBounds(true); image.setMaxHeight(dp(300)); image.setContentDescription("Demo prescription attachment preview");
            // Decode with a size bound, avoiding full-resolution prescription bitmaps.
            try {
                android.graphics.BitmapFactory.Options bounds=new android.graphics.BitmapFactory.Options(); bounds.inJustDecodeBounds=true;
                try(InputStream in=getContentResolver().openInputStream(Uri.parse(o.attachmentUri))) { android.graphics.BitmapFactory.decodeStream(in,null,bounds); }
                int sample=1; while(Math.max(bounds.outWidth,bounds.outHeight)/sample>1200) sample*=2;
                android.graphics.BitmapFactory.Options opts=new android.graphics.BitmapFactory.Options(); opts.inSampleSize=sample;
                try(InputStream in=getContentResolver().openInputStream(Uri.parse(o.attachmentUri))) { image.setImageBitmap(android.graphics.BitmapFactory.decodeStream(in,null,opts)); }
                rx.addView(image);
            } catch(Exception e) { text(rx,"Attachment unavailable. Reattach the demo image.",14,false); }
            button(rx,"Open full attachment",() -> {
                try { Intent view=new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(o.attachmentUri),"image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); startActivity(view); }
                catch(Exception e) { toast("No image viewer is available"); }
            });
        }
        if(o.status==Status.RECEIVED) {
            button(rx,"Attach a DEMO prescription image",() -> {
                Intent pick=new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE);
                pick.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                try { startActivityForResult(pick,901); } catch(Exception e) { toast("No document picker is available"); }
            });
            EditText note=input(rx,"Verification / rejection notes",o.reviewNote);
            CheckBox checked=new CheckBox(this); checked.setText("I reviewed patient identity, prescriber, medicine, dose, validity and refill authorisation."); rx.addView(checked);
            button(rx,"Verify prescription manually",() -> change(s -> s.verify(id,note.getText().toString(),checked.isChecked()),"Demo prescription verified"));
            button(rx,"Reject request",() -> confirm("Reject this request?",() -> change(s -> s.reject(id,note.getText().toString()),"Request rejected locally")));
        } else text(rx,"Review notes: "+o.reviewNote,15,false);
        if(o.status==Status.VERIFIED) button(page,"Start preparation and reserve stock",() -> change(s -> s.prepare(id),"Stock reserved locally"));
        if(o.status==Status.PREPARING) button(page,o.delivery?"Mark ready for delivery":"Mark ready for collection",() -> change(s -> s.advance(id),"Order marked ready locally"));
        if(o.status==Status.READY) {
            button(page,o.delivery?"Confirm dispatched":"Confirm patient collected",() -> confirm(o.delivery?"Confirm the order has been dispatched?":"Confirm this order has been collected?",() -> change(s -> s.advance(id),"Order status saved locally")));
            if(o.readyMessage.isEmpty()) button(page,"Create readiness notification (local outbox)",() -> change(s -> s.readyNotification(id),"Notification saved in local outbox — not sent"));
            else text(page,"Local notification: "+o.readyMessage+"\nDelivery to patient: NOT SENT",15,true).setTextColor(state.darkMode?Color.WHITE:0xFF00695C);
        }
        if(o.status==Status.DISPATCHED) button(page,"Confirm delivered",() -> confirm("Confirm delivery has been completed?",() -> change(s -> s.advance(id),"Delivery completed locally")));
        if(o.status!=Status.COMPLETED && o.status!=Status.CANCELLED && o.status!=Status.REJECTED && o.status!=Status.DISPATCHED) button(page,"Cancel order",() -> reasonDialog("Cancel order",reason -> change(s -> s.cancel(id,reason),"Cancelled; reserved stock restored")));
    }
    @Override protected void onActivityResult(int request,int result,Intent data) {
        super.onActivityResult(request,result,data);
        if(request!=901 || result!=RESULT_OK || data==null || data.getData()==null || selectedOrder.isEmpty() || state==null) return;
        Uri uri=data.getData(); String id=selectedOrder;
        try {
            String type=getContentResolver().getType(uri); if(type==null || !type.startsWith("image/")) throw new IllegalArgumentException("Select an image file");
            getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            change(s -> { Order o=s.order(id); if(o.status!=Status.RECEIVED) throw new IllegalArgumentException("Only received requests can be edited"); o.attachmentUri=uri.toString(); },"Demo image attached locally");
        } catch(Exception e) { toast("Could not retain access to that image. Choose a file from device storage."); }
    }
    private void inventory() {
        text(card(page,0xFFDDF2EC),"Medication inventory",21,true);
        text(page,"Stock = available packs. Preparation reserves stock once. Order prices are snapshots and do not change when the catalogue is edited.",14,false).setTextColor(state.darkMode?Color.WHITE:0xFF435851);
        button(page,"Add medication",() -> medicineDialog(null));
        for(Medicine m:state.medicines) {
            LinearLayout row=card(page,m.stock==0?0xFFFFE7E7:0xFFFFFFFF);
            text(row,m.name+" · "+m.strength,18,true);
            text(row,money(m.price)+" / pack · "+m.stock+" available"+(m.stock==0?" · OUT OF STOCK":""),15,false);
            button(row,"Edit price / available stock",() -> medicineDialog(m));
        }
    }
    private void medicineDialog(Medicine old) {
        LinearLayout form=form();
        EditText name=input(form,"Medicine name",old==null?"":old.name);
        EditText strength=input(form,"Strength and pack size",old==null?"":old.strength);
        EditText price=input(form,"Price per pack (rand)",old==null?"":java.math.BigDecimal.valueOf(old.price,2).toPlainString()); price.setInputType(8194);
        EditText stock=input(form,"Available packs",old==null?"0":String.valueOf(old.stock)); stock.setInputType(2);
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(old==null?"Add medication":"Edit medication").setView(form).setPositiveButton("Save",null).setNegativeButton("Cancel",null).create();
        dialog.setOnShowListener(v -> dialog.getButton(-1).setOnClickListener(b -> {
            try {
                String n=name.getText().toString().trim(), strengthValue=strength.getText().toString().trim();
                if(n.isEmpty() || strengthValue.isEmpty()) throw new IllegalArgumentException("Name and strength / pack size are required");
                long amount=cents(price.getText().toString()); int count=Integer.parseInt(stock.getText().toString());
                if(count<0 || count>1000000) throw new IllegalArgumentException("Stock must be 0 to 1,000,000 packs");
                String id=old==null?UUID.randomUUID().toString():old.id;
                change(s -> {
                    for(Medicine m:s.medicines) if(!m.id.equals(id) && m.name.equalsIgnoreCase(n) && m.strength.equalsIgnoreCase(strengthValue)) throw new IllegalArgumentException("This medicine and pack already exist");
                    if(old==null) s.medicines.add(new Medicine(id,n,strengthValue,amount,count));
                    else { Medicine m=s.medicine(id); m.name=n; m.strength=strengthValue; m.price=amount; m.stock=count; }
                },"Inventory saved locally"); dialog.dismiss();
            } catch(Exception e) { toast(e instanceof NumberFormatException?"Enter a whole number for stock":e.getMessage()); }
        })); dialog.show();
    }
    private void profile() {
        LinearLayout box=card(page,0xFFFFFFFF); text(box,"Pharmacy profile and opening hours",21,true);
        EditText name=input(box,"Pharmacy name",state.profile.name), address=input(box,"Location / street address",state.profile.address), phone=input(box,"Contact number",state.profile.phone), details=input(box,"Pharmacy details",state.profile.details);
        EditText opens=input(box,"Opens (HH:mm)",state.profile.opens), closes=input(box,"Closes (HH:mm)",state.profile.closes);
        CheckBox delivery=new CheckBox(this); delivery.setText("Offer delivery"); delivery.setChecked(state.profile.delivery); box.addView(delivery);
        EditText fee=input(box,"Delivery fee (rand)",java.math.BigDecimal.valueOf(state.profile.deliveryFee,2).toPlainString()); fee.setInputType(8194);
        button(box,"Save profile locally",() -> {
            String n=name.getText().toString().trim(),a=address.getText().toString().trim(),op=opens.getText().toString().trim(),cl=closes.getText().toString().trim();
            try {
                if(n.isEmpty()||a.isEmpty()) throw new IllegalArgumentException("Name and address are required"); hours(op,cl); long f=cents(fee.getText().toString());
                change(s -> { s.profile.name=n; s.profile.address=a; s.profile.phone=phone.getText().toString().trim(); s.profile.details=details.getText().toString().trim(); s.profile.opens=op; s.profile.closes=cl; s.profile.delivery=delivery.isChecked(); s.profile.deliveryFee=f; },"Profile saved locally");
            } catch(Exception e) { toast(e.getMessage()); }
        });
        text(box,"Times apply daily. A closing time earlier than opening means overnight opening. Existing demo orders retain their quoted delivery fees.",13,false);
        button(box,"Check address in Maps",() -> {
            Uri uri=Uri.parse("https://www.google.com/maps/search/").buildUpon().appendQueryParameter("api","1").appendQueryParameter("query",address.getText().toString()).build();
            try { startActivity(new Intent(Intent.ACTION_VIEW,uri)); } catch(Exception e) { toast("No Maps app or browser is available"); }
        });
    }
    private void demand() {
        LinearLayout box=card(page,0xFFFFFFFF); text(box,"Medication demand (demo orders)",21,true);
        for(Medicine m:state.medicines) {
            int requested=0,fulfilled=0,active=0;
            for(Order o:state.orders) for(Line l:o.lines) if(l.medicineId.equals(m.id)) {
                if(o.status!=Status.REJECTED && o.status!=Status.CANCELLED) requested+=l.quantity;
                if(o.status==Status.COMPLETED) fulfilled+=l.quantity;
                else if(o.status!=Status.REJECTED && o.status!=Status.CANCELLED) active+=l.quantity;
            }
            text(box,m.name+" · "+m.strength,17,true);
            text(box,"Requested: "+requested+" · Outstanding: "+active+" · Fulfilled: "+fulfilled+" · Available: "+m.stock,14,false);
        }
        LinearLayout log=card(page,0xFFE0F2EF); text(log,"Local action history",19,true);
        if(state.events.isEmpty()) text(log,"No changes recorded yet.",14,false);
        for(int i=state.events.size()-1;i>=Math.max(0,state.events.size()-30);i--) text(log,state.events.get(i),13,false);
    }
    private void outbox() {
        LinearLayout box=card(page,0xFFFFF1DD); text(box,"Readiness notifications — LOCAL OUTBOX",20,true);
        text(box,"Messages have not been sent to patients. Your backend must deliver them and return a delivery status.",15,false);
        int count=0;
        for(Order o:state.orders) if(!o.readyMessage.isEmpty()) { count++; text(box,o.patient+"\n"+o.readyMessage+"\nNOT SENT · Current order status: "+o.status,15,false); }
        if(count==0) text(box,"No messages yet. Open a ready order to create one.",15,false);
    }
    private void settings() {
        LinearLayout box=card(page,0xFFFFFFFF); text(box,"Settings",21,true);
        button(box,state.darkMode?"Use light background":"Use dark background",() -> change(s -> s.darkMode=!s.darkMode,"Appearance saved"));
        if(!demoPreview()) button(box,"Sign out",() -> confirm("Sign out of this account?",() -> { getSharedPreferences("ProteaHealthSession",MODE_PRIVATE).edit().clear().apply(); startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish(); }));
        button(box,"Reset this account's local demo data",() -> confirm("Delete local demo edits, orders and outbox messages?",() -> change(s -> { oldPharmacistState fresh=sample(); s.profile=fresh.profile; s.medicines=fresh.medicines; s.orders=fresh.orders; s.events=fresh.events; },"Demo data reset")));
    }
    private int dp(int x) { return Math.round(x*getResources().getDisplayMetrics().density); }
    private LinearLayout form() { LinearLayout f=new LinearLayout(this); f.setOrientation(1); f.setPadding(dp(20),dp(12),dp(20),dp(12)); return f; }
    private LinearLayout card(LinearLayout parent,int color) { LinearLayout box=form(); GradientDrawable bg=new GradientDrawable(); bg.setColor(color); bg.setCornerRadius(dp(18)); box.setBackground(bg); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.topMargin=dp(14); parent.addView(box,p); return box; }
    private TextView text(LinearLayout parent,String value,int size,boolean bold) { TextView v=new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(0xFF183A34); if(bold) v.setTypeface(null,Typeface.BOLD); v.setPadding(0,dp(5),0,dp(7)); parent.addView(v); return v; }
    private Button button(LinearLayout parent,String label,Runnable action) { Button b=new Button(this); b.setText(label); b.setTextSize(13); b.setAllCaps(false); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00796B)); b.setMinHeight(dp(48)); parent.addView(b); b.setOnClickListener(v->action.run()); return b; }
    private EditText input(LinearLayout parent,String label,String value) { TextView title=text(parent,label,14,true); EditText e=new EditText(this); e.setId(View.generateViewId()); title.setLabelFor(e.getId()); e.setSingleLine(true); e.setTextColor(parent==page && state!=null && state.darkMode ? Color.WHITE : 0xFF183A34); if(parent==page && state!=null && state.darkMode) title.setTextColor(Color.WHITE); e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00796B)); e.setText(value); parent.addView(e,new LinearLayout.LayoutParams(-1,-2)); return e; }
    interface TextAction { void run(String value); }
    private TextWatcher watch(TextAction action) { return new TextWatcher() { public void beforeTextChanged(CharSequence s,int a,int c,int f){} public void onTextChanged(CharSequence s,int a,int b,int c){action.run(s.toString());} public void afterTextChanged(Editable e){} }; }
    private void reasonDialog(String title,TextAction action) { LinearLayout f=form(); EditText e=input(f,"Reason",""); new AlertDialog.Builder(this).setTitle(title).setView(f).setPositiveButton("Confirm",(d,w)->action.run(e.getText().toString())).setNegativeButton("Back",null).show(); }
    private void confirm(String message,Runnable action) { new AlertDialog.Builder(this).setMessage(message).setPositiveButton("Confirm",(d,w)->action.run()).setNegativeButton("Back",null).show(); }
    private void toast(String message) { Toast.makeText(this,message==null?"Unable to perform action":message,Toast.LENGTH_LONG).show(); }
}
