package com.hllbr.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
        Bitmap selectedImage;
        ImageView imageView;
        EditText postText;
        private FirebaseStorage firebaseStorage ;
        private StorageReference storageReference;
        Uri imageData;
        //Veri tabanıyla çalışmak için yine üst satırlardaki yöntemlere benzer bir yol izliyorum
        private FirebaseFirestore firebaseFirestore;
        FirebaseAuth firebaseAuth ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        postText = findViewById(R.id.commentText);
        imageView = findViewById(R.id.imageView);

        firebaseStorage = FirebaseStorage.getInstance();
        //Nereye ne kaydedeceğimi tek tek belirtmem gerekiyor.Bunu yapmak için referans denilen bir yapıyı kullanmam gerekiyor
        storageReference = firebaseStorage.getReference();
        setTitle("Post Upload Screen");

        //Database actions =
        firebaseFirestore = FirebaseFirestore.getInstance();//objeyi initiliaze ettim şimdi emaili alıcam ve bu şekilde devam edeceğim
        //Email almak için güncel kullanıcıyı almam gerekiyor.
        firebaseAuth = FirebaseAuth.getInstance();





    }
    public void selectImage(View view){//Apı 23 ve öncesinde uyumlu olabilmesi için ContextCompeti kulalnıyoruz.Apı 23 öncesinde izin istenmesi gerekmiyordu

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //eğer izin yoksa
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intentToGallery =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }

    }
    public void pushButton(View view){
        if(imageData != null){
            //uui =Universal unique id = uluslararası kendine has bir id olarak ifadev edebilirim
            UUID uuid = UUID.randomUUID();
            final String imageName1 = "images/"+uuid+"jpg";

            storageReference.child(imageName1).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                /*
                storageReference.child("images").child("images2").putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 */
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*
                    Bu alana storage içerisinde kayıtlı olan resimin urlsini firebasefirestore içine kaydedeceğim Bu işlem için yükleme url'sini almama gerek ...
                     */
                    //Dowland Url = almak için bir referansa daha ihtiyacım var

                    StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName1);//imageName1 ifadesinin depo içerisinde nere kaydedildiğini bul olarak ifade edebilrim
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            System.out.println("URI TEST = "+downloadUrl);


                            //Bitmapleri küçültme işlemini bu projede yapmadık sqlite da olduğu gibi burada bir sorun oluşturmaz .Büyük megabytelık şeyleride kaydedebiliriz
                            //5 mbyte lık bir resmi instagramda paylaşmak isterse yavaş olur ama olur

                            //Kullanıcıyı alma işlemi için authentication kullanıyorduk burada başlıyorum

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();//mevcut kullanıcıyı aldım
                            String userEmail = firebaseUser.getEmail();
                            String comment = postText.getText().toString();
                            if(comment.matches("")){
                                Toast.makeText(UploadActivity.this,"message field cannot be empty",Toast.LENGTH_LONG).show();
                            }else{
                                HashMap<String,Object> postData = new HashMap<String, Object>();
                                postData.put("userEmail",userEmail);
                                postData.put("downloadUrl",downloadUrl);
                                postData.put("comment",comment);//Post
                                postData.put("date", FieldValue.serverTimestamp());//Serverdaki güncel zamanı yaptığın işlem için zamanı ... bize veriyor

                                firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    //üst satır işlemi için bir hashmap gerektiği için bir hasmap meydana getiriyorum
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        //İşlem başarılıysa kullanıcıyı feed activitye götüreceğim
                                        Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Bütün açık activityleri kapat gibi bir yapımız söz konusu
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UploadActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }
                    });



                    Toast.makeText(UploadActivity.this,"Operation is success:)",Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {//izinlerin sonucunun kontrolü ne oldu
        if(requestCode == 1 ){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               startActivityForResult(intentToGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//başlatılan activity sonucu veriliyor
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            //burada verilen data verisini bir uriy'e dönüştürmemiz gerekiyor
             imageData = data.getData();//veri henüz görsel değil çevimemiz gerekecek
            try {//SDK 28 değişikliğini göz önünde bulundurarak işlemlerimi gerçekleştirmem gerekiyor
            if(Build.VERSION.SDK_INT >=28){
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                selectedImage = ImageDecoder.decodeBitmap(source);
                imageView.setImageBitmap(selectedImage);
            }else{
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                imageView.setImageBitmap(selectedImage);
            }
            } catch (IOException e) {
                e.printStackTrace();
            }

            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*public Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        //bu metodun bana bir adet bitmap dönmesinini istediğim için bitmap sınıfından yararlanarak metodu oluşturuyorum
        //orantılı olarak bir küçültme işlemi gerçekleştirmek istiyorum.Bunu sorgularla yapabilirim

        int width = image.getWidth();
        int height = image.getHeight();
        //hassas bir sonuç elde etmek için double/float ile işlemlerime devam ediyorum

        float bitmapRatio = (float)(width/height);
        //bitmapRatio eğer 1 den büyükse widht daha büyük demektir.Genişlik daha büyük resim yatay büyük demek
        if(bitmapRatio > 1){
            width = maximumSize;
            height = (int)(width/bitmapRatio);
        }else{
            height = maximumSize;
            width = (int)(height*bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);


    }*/
}