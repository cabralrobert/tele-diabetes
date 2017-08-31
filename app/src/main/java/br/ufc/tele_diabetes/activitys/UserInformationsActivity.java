package br.ufc.tele_diabetes.activitys;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.ufc.tele_diabetes.R;
import br.ufc.tele_diabetes.utils.Mask;
import br.ufc.tele_diabetes.utils.User;
import br.ufc.tele_diabetes.utils.Utils;

public class UserInformationsActivity extends AppCompatActivity implements View.OnClickListener {

    boolean sexo = false;
    String tipoDiabetesS;

    EditText nome, cpf, telefone, rua, numeroCasa, estado, cidade, bairro, cep, idade;
    RadioButton masculino, feminino, tipo1, tipo2, gestacional;
    Switch cardiaco;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    Button botao;

    ImageView imageView;
    User us = new User();
    public static final int IMAGE_SDCARD = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_informations);

        Utils.getDatabase();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        imageView = (ImageView)findViewById(R.id.userimg);

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

        if(us.getImage() != null) {
            UploadTask uploadTask = riversRef.putFile(us.getImage());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao enviar a foto", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();
                }
            });
        }

        us.setImage(null);

        databaseReference.child("users").child(user.getUid()).setValue(us);

        Toast.makeText(getApplicationContext(), "Informações atualizadas!!!", Toast.LENGTH_LONG).show();

        finish();
        startActivity(new Intent(UserInformationsActivity.this, UserActivity.class));

    }

    @Override
    public void onClick(View view) {
        if (view == botao){
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
                us.setImage(image);
                imageView.setImageURI(image);
            }
        }
    }
}
