package br.ufc.tele_diabetes.activitys;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import br.ufc.tele_diabetes.R;
import br.ufc.tele_diabetes.utils.DadosSensor;
import br.ufc.tele_diabetes.utils.ItemDashboard;
import br.ufc.tele_diabetes.utils.TestService;
import br.ufc.tele_diabetes.utils.User;
import br.ufc.tele_diabetes.utils.Utils;

import static android.R.attr.data;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    LineGraphSeries<DataPoint> series;
    GraphView graph, graph2;
    FirebaseUser user;
    DatabaseReference databaseReference;
    User usuario;
    private ProgressDialog progressDialog;
    Toolbar toolbar;
    File localFile = null;
    ImageView imageUser;
    ArrayList<DadosSensor> tes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        startService(new Intent(UserActivity.this, TestService.class));

        Utils.getDatabase();

        List<ItemDashboard> details  = getListData();
        final ListView listView = (ListView)findViewById(R.id.lista);
        listView.setAdapter(new CustomListActivity(this, details));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0){
                    startActivity(new Intent(UserActivity.this, SettingsActivity.class));
                }else if(i == 1){
                    startActivity(new Intent(UserActivity.this, EditProfileActivity.class));
                }else{
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                }

            }
        });

        imageUser = (ImageView)findViewById(R.id.imageUser);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        graph = (GraphView)findViewById(R.id.grafico);
        graph2 = (GraphView)findViewById(R.id.grafico2);

        //initGrafico("Sensor 1", graph);
        //initGrafico("Sensor 2", graph2);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.keepSynced(true);
        user = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        progressDialog.setMessage("Colhendo informações...");
        progressDialog.show();

        user = firebaseAuth.getCurrentUser();

        if(user != null) {
            databaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usuario = dataSnapshot.getValue(User.class);
                    progressDialog.dismiss();
                    if(usuario == null){
                        finish();
                        startActivity(new Intent(UserActivity.this, UserInformationsActivity.class));
                    }else {
                        toolbar.setTitle(usuario.getNome());
                        setSupportActionBar(toolbar);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(user != null) {
            databaseReference.child("dados").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        DadosSensor post = postSnapshot.getValue(DadosSensor.class);
                        if(post != null) {
                            tes.add(post);
                            Log.i("Get", "Get: " + post.getValue());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //imagem
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =storage.getReferenceFromUrl("gs://tele-diabetes.appspot.com");
        StorageReference islandRef = storageRef.child(user.getUid());


        try {
            localFile = File.createTempFile("images", "*");
        } catch (IOException e) {
            e.printStackTrace();
        }

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Uri uri = Uri.fromFile(localFile);
                if(uri != null) {
                    usuario.setImage(uri);
                    imageUser.setImageURI(uri);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Erro ao pegas as informações", Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                atualizaGrafico();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

//                Intent i = new Intent(UserActivity.this, UserActivity.class);
//
//                Bitmap bp = BitmapFactory.decodeResource(getResources(), R.mipmap.iconnot);
//
//                PendingIntent pi = PendingIntent.getActivity(getBaseContext(),1,i,PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(UserActivity.this)
//                        .setLargeIcon(bp)
//                        .setSmallIcon(R.mipmap.iconnot)
//                        .setContentText(usuario.getNome())
//                        .setContentTitle("Tele-Diabetes")
//                        .setContentIntent(pi);
//
//                Notification notification = builder.build();
//                NotificationManager nm = (NotificationManager) UserActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
//                nm.notify(1,notification);

            }
        });

    }

    private List<ItemDashboard> getListData(){
        List<ItemDashboard> list = new ArrayList<ItemDashboard>();
        ItemDashboard setting = new ItemDashboard("Configurações", "st");
        ItemDashboard edPerfil = new ItemDashboard("Editar Perfil", "et");
        ItemDashboard sair = new ItemDashboard("Sair", "s");

        list.add(setting);
        list.add(edPerfil);
        list.add(sair);

        return list;
    }

    private void initGrafico(String nome, GraphView graph){

        graph.setTitle(nome);

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1024);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-5);
        graph.getViewport().setMaxX(100);


        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);
    }

    private void atualizaGrafico() {

//        series = new LineGraphSeries<DataPoint>();
//
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    Socket s = new Socket("192.168.0.101", 5561);
//
//                    InputStream i = s.getInputStream();
//                    Scanner sc = new Scanner(i);
//                    for(int j = 0; sc.hasNext(); j++){
//                        if(sc != null) {
//                            double value = Double.parseDouble(sc.next());
//                            series.appendData(new DataPoint(j, value), true, 500);
//                            System.out.println("Valor: " + value);
//                        }
//                    }
//                    Thread.currentThread().sleep(200);
//                    graph.addSeries(series);
//                    s.close();
//
//                } catch (IOException e){
//                    System.out.println(e.getMessage());
//                } catch (ConcurrentModificationException e){
//                    System.out.println(e.getMessage());
//                } catch (InterruptedException e){
//                    System.out.println(e.getMessage());
//                }
//            }
//        });
//        t.start();
//    }

        graph.setTitle("Teste");

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(70);
        graph.getViewport().setMaxY(1000);

//        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.5);
//        graph.getViewport().setMaxX(5.5);


//        graph.getViewport().setScalable(true);
//        graph.getViewport().setScalableY(true);
//        graph.getViewport().setScrollable(true);
//        graph.getViewport().setScrollableY(true);

//        Log.i("Valor","Valor: " + dt.format(d));

        BarGraphSeries<DataPoint> barseries = new BarGraphSeries<>(dados());

        barseries.setAnimated(true);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(barseries.getHighestValueY() + 0.5);
        graph.getViewport().setMinX(barseries.getLowestValueX() - 0.5);
        graph.getViewport().setMaxX(barseries.getHighestValueX() + 0.5);

        barseries.setSpacing(5);
        barseries.setDrawValuesOnTop(true);
        barseries.setValuesOnTopColor(Color.BLACK);
        barseries.setValuesOnTopSize(20);

        graph.addSeries(barseries);

        barseries.setValueDependentColor((new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        }));

        barseries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getApplicationContext(), "Clicou no: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public DataPoint[] dados(){
        int n=tes.size();
        Log.i("Tamanho",""+n);
        DataPoint[] values = new DataPoint[n];     //creating an object of type DataPoint[] of size 'n'
        for(int i=0;i<n;i++){
            DataPoint v = new DataPoint((double)tes.get(i).getId(),(double)tes.get(i).getValue());
            Log.i("Valores",""+tes.get(i).getId() + " | " + tes.get(i).getValue());
            values[i] = v;
        }
        return values;
    }
}
