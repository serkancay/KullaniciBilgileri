package com.serkancay.kullanicibilgileri;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tvAdSoyad, tvMail, tvPhoneNumber, tvSerial, tvOperator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Öğelerimizi XML tarafından çağırıyoruz.
        prepareComponents();

        // Uygulama hassas bilgilere erişeceği için kullanıcıya bir bilgilendirme yapıyoruz.
        // Bunu yapmazsak uygulamamız markette yayınlanmaz ve askıya alınır.
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
        .setTitle("DİKKAT!");
        dialog.setMessage("Bu uygulamada kişisel verilerinize iletişim kurma amacıyla erişilmektedir.");
        dialog.setPositiveButton("Kullanılsın", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Kullanıcı izin verirse setDatas() metodu ile bilgilere ulaşıyoruz.
                setDatas();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("İzin Vermiyorum", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Kullanıcı izin vermezse cleanScreen() metodu ile öğeleri gizliyoruz.
                cleanScreen();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void setDatas(){
        // Fonksiyonlarımızı kullanarak TextView'ları dolduruyoruz.
        tvAdSoyad.setText("Ad - Soyad: " + getUserName());
        tvMail.setText("E-posta: " + getUserMail());
        tvPhoneNumber.setText("Telefon: " + getUserPhoneNumber());
        tvSerial.setText("Sim Seri No: " + getUserSimSerial());
        tvOperator.setText("Operatör: " + getUserNetworkOperator());
    }

    private void cleanScreen(){
        // Kullanıcı izin vermediyse öğeleri gizleyelim. Ve ilk öğeye mesaj verelim.
        tvAdSoyad.setText("İZİN ALINAMADI!");
        tvMail.setVisibility(View.GONE);
        tvPhoneNumber.setVisibility(View.GONE);
        tvSerial.setVisibility(View.GONE);
        tvOperator.setVisibility(View.GONE);
    }

    private void prepareComponents(){
        tvAdSoyad = (TextView) findViewById(R.id.tvAdSoyad);
        tvMail = (TextView) findViewById(R.id.tvMail);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        tvSerial = (TextView) findViewById(R.id.tvSerial);
        tvOperator = (TextView) findViewById(R.id.tvSimOperator);
    }

    public String getUserName() {
        // Bu metod kullanıcı adını dönderir.
        String userName = "Bulunamadı.";

        // Bir cursor nesnesi ile kullanıcı bilgilerine ulaşıyoruz.
        Cursor c = getApplication().getContentResolver()
                .query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        // Cursor'un null olma ihtimali var kontrol etmek faydalı.
        if(c != null){
            // Cursor boş bir index dönebilir ve uygulama çökebilir kontrol şart.
            if(c.getCount() > 0) {
                c.moveToFirst();
                // Verilerden display_name kolonu bize kullanıcı adını verir.
                userName = c.getString(c.getColumnIndex("display_name"));
            }
            // Cursor'u kapatalım.
            c.close();
        }
        return userName;
    }

    public String getUserMail(){
        // Bu metod kullanıcının e-postasını dönderir.
        String userEmail = "Bulunamadı.";
        // Hesaplara ulaşalım. Burada tip olarak google'u seçtik. Nedeni büyük ihtimalle
        // bir gmail hesabının bulunacak olması.
        Account[] accounts =
                AccountManager.get(this).getAccountsByType("com.google");
        for (Account account : accounts) {
            userEmail = account.name;
        }
        return userEmail;
    }

    public String getUserPhoneNumber(){
        // Bu metod kullanıcının telefon numarasını EĞER TANIMLIYSA dönderir.
        String userPhone;
        // TelephonyManager nesnesi oluşturduk.
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        // getLine1Number metodu bize kullanıcının telefon numarasını verir.
        // Eğer telefon numarası bulunmazsa "Bulunamadı" şeklinde sonuç dönderdik.
        userPhone = tMgr.getLine1Number() != null && !tMgr.getLine1Number().trim().equals("") ? tMgr.getLine1Number() : "Bulunamadı.";
        Log.d("PHONE", userPhone);
        return userPhone;
    }

    public String getUserSimSerial(){
        String userSerial;
        // TelephonyManager nesnesi oluşturduk.
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        // getSimSerialNumber ile seri numarasını aldık.
        userSerial = tMgr.getSimSerialNumber() != null && !tMgr.getSimSerialNumber().trim().equals("") ? tMgr.getSimSerialNumber() : "Bulunamadı";
        return userSerial;
    }

    public String getUserNetworkOperator(){
        String userOperator;
        // TelephonyManager nesnesi oluşturduk.
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        // getNetworkOperatorName ile operatör adını aldık.
        userOperator = tMgr.getNetworkOperatorName() != null && !tMgr.getNetworkOperatorName().trim().equals("") ? tMgr.getNetworkOperatorName() : "Bulunamadı.";
        return userOperator;
    }

}