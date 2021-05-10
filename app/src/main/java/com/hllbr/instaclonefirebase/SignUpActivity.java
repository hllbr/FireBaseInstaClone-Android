package com.hllbr.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;//bir sonraki aşamada sınıftan bir obje  oluşturmak için getinstance'tan yararlanacağım.
    //bu noktada objeyi tanımladım initialize etmemiştim.Değer atamsaı yapmadım.
    EditText emailName ,passwordName;
        /*
        Bu alan Kullanıcı arayüzü olacak kullanıcı girişi butonu yada yeni kullanıcı oluşturmak için bir buton olacak
        Kullanıcı adının ve şifrenin girileceği bir başka alana daha ihtiyacım olacak
        *FireBase de tüm modüllerde işlem yapmadan önce süreçleri başlatmam gerekiyor.we need operation initialize

         */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupactivity);

        firebaseAuth = FirebaseAuth.getInstance();//Değer atamasını burada gerçekleştiriyorum
        emailName = findViewById(R.id.emailNameText);
        passwordName = findViewById(R.id.passwordNameText);

        /*
        İçeri giriş yapmış bir kullanıcı varmı kontrol etmek istiyorum
         */
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();//bu şekilde içeri giriş yapmış kullanıcıyı alabiliyoruz
        //Eğer içeri giriş yapmış bir kullanıcı yoksa null dönüyor
        if(firebaseUser != null){
            //bir kullanıcı varsa giriş yapmış gerekli işlemler yani feed ekranına aktarma
            Intent intent = new Intent(SignUpActivity.this,FeedActivity.class);
            startActivity(intent);
            Toast.makeText(SignUpActivity.this,"Welcome!",Toast.LENGTH_LONG).show();
            finish();
        }

    }
    public void signInClicked(View view){
        //Giriş yap butonu basıldığında kullanıcı kayılı mı sorgulamamız gerekiyor eğer kullanıcı kayıtlıysa gişir yaptırmalı değilse kayıt olması için bir toast mesajı götererek uyarmalıyım.
        //Kullanıcıyı kontrol ederek bir sonraki activity'e geçiş sağlayacağım
        String email = emailName.getText().toString();
        String password = passwordName.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(SignUpActivity.this,FeedActivity.class);
                startActivity(intent);
                Toast.makeText(SignUpActivity.this,"Welcome!",Toast.LENGTH_LONG).show();
                finish();//geri basmam demek uygulamanın kapanması demek bu sayede

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public void sıgnUpClicked(View view){
        //Kullanıcıyı veri tabanına kaydetmek için gerekli işlemlerin gerçekleştirilmesi için kullanacağım alan
        //FireBaseAuthentication ile çalışırken döküman takip ederek gitmek gerekiyor bu işlemler için oluşturulmuş sınıfları ve adımları takip etmek gerekiyor.
       //Tanımlamaları yaptıktan sonra burada email alanı veya password alanının boş olup olmadığıyla ilgili bir sorgulama yazabilirim
        String email = emailName.getText().toString();
        String password = passwordName.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
        //başarılı bir şekilde işlemler yürütülürse olacaklar =
        Toast.makeText(SignUpActivity.this,"User Created...!",Toast.LENGTH_LONG).show();
        //normal şartlarda giriş başarılı olursa kullanıcı başka bir aktiviteye götürmem gerekiyor .üst satır ile test gerçekleştiriyorum
        Intent intent = new Intent(SignUpActivity.this,FeedActivity.class);
        startActivity(intent);
        finish();
        }
        }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //işlemlerden birinde hata meydana gelirse yapıacaklar =
            //burarada hata mesajımız olan e ifadesi firebasen geliyor hatanın internet kesintisi veya başka bir nedenden kaynaklandığı durumda bu durumu bana bildirebirebilecek bir yapıda .
            //FireBase mesajları ile kullanıcıya hatasının nerede olduğunu göstererek uygun girişi yapması için onu yönlendirebilirim
            //Giriş işlemi başarısız olduğunda durumla ilgili mesajı gösteriyorum başka bir işleme gerek duymuyorum bu alan için
            Toast.makeText(SignUpActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
        }
    });
    //istenen parametreler biri email biri password bu verileri editTextler üzerinden almam gerekiyor.
    //Burada kullanıcıyı oluştudumfakat gerçekten oluştumu daha önce var olan email ile mi giriş yapmaya çalıştı
    //daha önce kullanılmış bir mail ile mi kullanıcı oluşturmaya çalışılıyor
    //internet bağlantısı kesildi mi
    //Gibi senaryoları kontrol etmem gerekiyorki müdehale kodlarını yazabileyim
    //code add function oalrak biliyor.Listener dinleyiciler işlem bittikten sonra bize bir cevap dönmesini istiyoruz şu durumda
    //cancelledlistener işlem iptal edilirse bana bazı bilgiler veriyor
    //failureListener işlem hatalı bir şekilde gerçekleşirse bana bunun bilgilerini veriyor
    //successListener işlem başarılı bir şekilde yürütülürse bana uygun şekilde dönüyor
    //compliteListener işlem tamamlanınca cağrılıyor


    /*
    genelde completeListener kullanılır .Bir hata mesajı yada başka bir işlem yapmak istemiyorsanız
    Yad hem failure hemde success birlikte kullanırız iki durumuda göz önünde bulundurarak

     */
    }
}