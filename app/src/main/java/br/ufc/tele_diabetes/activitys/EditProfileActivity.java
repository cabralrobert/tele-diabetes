package br.ufc.tele_diabetes.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import br.ufc.tele_diabetes.R;
import br.ufc.tele_diabetes.utils.DadosSensor;
import br.ufc.tele_diabetes.utils.Mask;
import br.ufc.tele_diabetes.utils.User;
import br.ufc.tele_diabetes.utils.Utils;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    boolean sexo = false;
    String tipoDiabetesS;

    FirebaseAuth firebaseAuth;

    EditText nome, cpf, telefone, rua, numeroCasa, estado, cidade, bairro, cep, idade;
    RadioButton masculino, feminino, tipo1, tipo2, gestacional;
    Switch cardiaco;

    Button botao;

    FirebaseUser user;
    DatabaseReference databaseReference;
    User usuario;
    private ProgressDialog progressDialog;

    ImageView imageView;
    public static final int IMAGE_SDCARD = 12;

    File localFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getDatabase();

        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        nome = (EditText)findViewById(R.id.nameUser);
        cpf = (EditText)findViewById(R.id.cpfUser);
        telefone = (EditText)findViewById(R.id.telefoneUser);
        rua = (EditText)findViewById(R.id.ruaUser);
        numeroCasa = (EditText)findViewById(R.id.numeroCasaUser);
        estado = (EditText)findViewById(R.id.estadoUser);
        cidade = (EditText)findViewById(R.id.cidadeUser);
        bairro = (EditText)findViewById(R.id.bairroUser);
        cep = (EditText)findViewById(R.id.cepUser);
        idade = (EditText)findViewById(R.id.idadeUser);

        cpf.addTextChangedListener(Mask.insert(Mask.CPF_MASK, cpf));
        telefone.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, telefone));
        cep.addTextChangedListener(Mask.insert(Mask.CEP_MASK, cep));

        masculino = (RadioButton) findViewById(R.id.radioButtonMasculino);
        feminino = (RadioButton) findViewById(R.id.radioButtonFeminino);
        tipo1 = (RadioButton) findViewById(R.id.radioButtonTipo1);
        tipo2 = (RadioButton) findViewById(R.id.radioButtonTipo2);
        gestacional = (RadioButton) findViewById(R.id.radioButtonGestacional);

        cardiaco = (Switch)findViewById(R.id.cardiaco);

        botao = (Button)findViewById(R.id.botaoAtualizar);

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
                        startActivity(new Intent(EditProfileActivity.this, UserInformationsActivity.class));
                    }else {
                        nome.setText(usuario.getNome());
                        cpf.setText(usuario.getCpf());
                        telefone.setText(usuario.getTelefone());
                        idade.setText(usuario.getIdade());
                        if(!usuario.isSexo()) {
                            feminino.setChecked(false);
                            masculino.setChecked(true);
                        }else{
                            feminino.setChecked(true);
                            masculino.setChecked(false);
                        }
                        rua.setText(usuario.getRua());
                        numeroCasa.setText(usuario.getNumeroCasa());
                        estado.setText(usuario.getEstado());
                        cidade.setText(usuario.getCidade());
                        bairro.setText(usuario.getBairro());
                        cep.setText(usuario.getCep());

                        if(usuario.getDiabetes() == "Tipo 1"){
                            tipo1.setChecked(true);
                        }else if(usuario.getDiabetes() == "Tipo 2"){
                            tipo2.setChecked(true);
                        }else{
                            gestacional.setChecked(true);
                        }

                        cardiaco.setChecked(usuario.isDoencaCardiaca());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
                }
            });

        }

        imageView = (ImageView)findViewById(R.id.userimgedit);

        //imagem
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =storage.getReferenceFromUrl("gs://tele-diabetes.appspot.com");
        StorageReference islandRef = storageRef.child(user.getUid());

        if(localFile == null) {
            try {
                localFile = File.createTempFile("images", "*");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Já criado", Toast.LENGTH_LONG).show();
        }

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Uri uri = Uri.fromFile(localFile);
                if(uri != null) {
                    usuario.setImage(uri);
                    imageView.setImageURI(uri);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Erro ao pegas as informações", Toast.LENGTH_LONG).show();
            }
        });

        imageView.setOnClickListener(this);
        botao.setOnClickListener(this);

    }

    public void atualizaDados(){
        String nomeS, cpfS, telefoneS, ruaS, numeroCasaS,estadoS, cidadeS, bairroS, cepS, idadeS;
        boolean cardioco;

        nomeS = nome.getText().toString().trim();
        cpfS = cpf.getText().toString().trim();
        telefoneS = telefone.getText().toString().trim();
        ruaS = rua.getText().toString().trim();
        numeroCasaS = numeroCasa.getText().toString().trim();
        estadoS = estado.getText().toString().trim();
        cidadeS = cidade.getText().toString().trim();
        bairroS = bairro.getText().toString().trim();
        cepS = cep.getText().toString().trim();
        idadeS = idade.getText().toString().trim();

        cardioco = cardiaco.isChecked();

        RadioGroup r = (RadioGroup)findViewById(R.id.sexo);
        RadioGroup tipo = (RadioGroup)findViewById(R.id.doencadiabetes);

        r.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.radioButtonMasculino:
                        sexo = false;
                        break;

                    case R.id.radioButtonFeminino:
                        sexo = true;
                        break;
                }
            }
        });

        tipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.radioButtonTipo1:
                        tipoDiabetesS = "Tipo 1";
                        break;

                    case R.id.radioButtonTipo2:
                        tipoDiabetesS = "Tipo 2";
                        break;

                    case R.id.radioButtonGestacional:
                        tipoDiabetesS = "Gestacional";
                        break;
                }
            }
        });

        User us = new User();

        us.setNome(nomeS);
        us.setTelefone(telefoneS);
        us.setCpf(cpfS);
        us.setIdade(idadeS);
        us.setRua(ruaS);
        us.setNumeroCasa(numeroCasaS);
        us.setEstado(estadoS);
        us.setCidade(cidadeS);
        us.setBairro(bairroS);
        us.setCep(cepS);
        us.setDiabetes(tipoDiabetesS);
        us.setDoencaCardiaca(cardioco);
        us.setSexo(sexo);


        FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://tele-diabetes.appspot.com");
        StorageReference riversRef = storageRef.child(user.getUid());

        if(usuario.getImage() != null) {
            UploadTask uploadTask = riversRef.putFile(usuario.getImage());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao enviar a foto", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();
                }
            });
        }

        us.setImage(null);

        ArrayList<DadosSensor> tes = new ArrayList<>();

        Random rand = new Random();

        tes.add(new DadosSensor(1,rand.nextInt(1000)));
        tes.add(new DadosSensor(2,rand.nextInt(1000)));
        tes.add(new DadosSensor(3,rand.nextInt(1000)));
        tes.add(new DadosSensor(4,rand.nextInt(1000)));
        tes.add(new DadosSensor(5,rand.nextInt(1000)));
        tes.add(new DadosSensor(6,rand.nextInt(1000)));


        databaseReference.child("users").child(user.getUid()).setValue(us);
        databaseReference.child("dados").child(user.getUid()).setValue(tes);

        Toast.makeText(getApplicationContext(), "Informações atualizadas!!!", Toast.LENGTH_LONG).show();

        finish();
        startActivity(new Intent(EditProfileActivity.this, UserActivity.class));

    }

    @Override
    public void onClick(View view) {
        if(view == botao){
            atualizaDados();
        }
        else if(view == imageView){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,IMAGE_SDCARD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode == IMAGE_SDCARD){
            if(resultCode == RESULT_OK){
                Uri image = intent.getData();
                usuario.setImage(image);
                imageView.setImageURI(image);
            }
        }
    }
}
