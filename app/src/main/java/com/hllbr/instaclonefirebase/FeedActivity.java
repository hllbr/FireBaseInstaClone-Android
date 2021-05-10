package com.hllbr.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    /*
    Burada oluşturduğum itemlerin içinde bulunduğu menuyu bağlamam için inflate yani şişirmeme ve hangi itemin seçildiğini belirlemem gerekiyor.

     */
    private FirebaseAuth firebaseAuth ;
    private FirebaseFirestore firebaseFirestore ;
    ArrayList<String> userEmailFromFB;
    ArrayList<String> userCommentFromFB;
    ArrayList<String> userImageFromFB;
    FeedRecyclerAdapter feedRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        firebaseAuth = FirebaseAuth.getInstance();
        setTitle("Your Post Screen");
        firebaseFirestore = FirebaseFirestore.getInstance();
        userCommentFromFB = new ArrayList<>();
        userEmailFromFB = new ArrayList<>();
        userImageFromFB = new ArrayList<>();
        getDataFromFireStore();
        //RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerAdapter = new FeedRecyclerAdapter(userEmailFromFB,userCommentFromFB,userImageFromFB);
        recyclerView.setAdapter(feedRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//Menüyü bağlamak için
        MenuInflater inflater = getMenuInflater();
        //xml dosyası oluşturup onu kod ile bağlamamız gereken durumlarda kullandığımız yapılar Inflate yapıları

        inflater.inflate(R.menu.insta_options_menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//Menü üzerinde hangi itemin seçildiğini anlamak için
        if(item.getItemId() == R.id.add_post){
            //post ekleme seçeneği tıklanmışsa/seçilmişse....
            Intent intentToUpLoad = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpLoad);

        }else if(item.getItemId() == R.id.signout){
            //çıkış seçeneği tıklanmışsa/seçilmişse
            //Firebaseden signuot olabileceğime emin olmama gerekiyor .Bunun kontrolünü gerçekleştirerek devam etmeliyim .Bunun için firebaseAuth oluşturmalıyım
            AlertDialog.Builder alert = new AlertDialog.Builder(FeedActivity.this);
            alert.setTitle("Sign Out ?");
            alert.setMessage("Are you sure you want to log out?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        firebaseAuth.signOut();//eskiden bu çıkış yapısı try-catch içinde yazılıyordu kaldırıldı ben yinede try catch içinde yazıyorum
                        Intent intentTogetOut = new Intent(FeedActivity.this,SignUpActivity.class);
                        startActivity(intentTogetOut);
                        finish();//çıkıştan sonra geri dönmeyi engellemek için koyduk

                        Toast.makeText(FeedActivity.this,"Exit from the activity was successful.",Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FeedActivity.this,"exit process aborted",Toast.LENGTH_LONG).show();
                }
            });
            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }
    public void getDataFromFireStore(){
        //burada firestore değişkenini çağıracağız.Bunu yapmanın birçok yolu var
        //1. yöntem = bir kez almak için
        //firebaseFirestore.collection("Posts").get();
        //2. yontem =
        //firebaseFirestore.collection("Posts").addSnapshotListener()
        //3.yontem =storagereferansce gibi firebasefirestore içindeki referansı bulmak için kullanılan bir referans

        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        //collectionReference.get() veya ..
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //Bütün veriler bu snapshot içerisinde yüklü bu snapshot sürekli güncelliniyor veri tabanı değiştikçe burada da değişiklikler oluyor
                //eğer veri tabanı okunamazsa yapılmasını istediğim birşey varsa yazdırabilirim.
                if(error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

               // value.getDocuments()//bu snapshotları bize liste olarak veren bir yapı
                if (value != null){
                    for (DocumentSnapshot snapshot :value.getDocuments()){
                        Map<String,Object>  data = snapshot.getData();//kaydettiğim dataya ulaşmış oldum
                        //Casting
                        String coment =(String) data.get("comment");
                        String usere = (String)data.get("userEmail");
                        String downURL =(String)data.get("downloadUrl");

                        //Test =
                        System.out.println("Test comment = "+coment);
                        System.out.println("Test usere = "+ coment);
                        System.out.println("Test downUrl = "+downURL);
                        userCommentFromFB.add(coment);
                        userEmailFromFB.add(usere);
                        userImageFromFB.add(downURL);

                        feedRecyclerAdapter.notifyDataSetChanged();
                    }
                }


            }
        });
    }
}