package br.ufc.tele_diabetes.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.ufc.tele_diabetes.R;
import br.ufc.tele_diabetes.utils.TestService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button botaoLogin;
    private EditText edEmail, edSenha;
    private TextView tvCadastro;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        botaoLogin = (Button)findViewById(R.id.botaoLogin);
        edSenha = (EditText) findViewById(R.id.passwordLogin);
        edEmail = (EditText) findViewById(R.id.emailLogin);
        tvCadastro = (TextView)findViewById(R.id.facaCadastro);

        botaoLogin.setOnClickListener(this);
        tvCadastro.setOnClickListener(this);

    }

    private void loginUser(){
        String email = edEmail.getText().toString().trim();
        String senha = edSenha.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // campo de email vazio
            Toast.makeText(this,"Digite o email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(senha)){
            // campo de senha vazio
            Toast.makeText(this,"Digite a senha", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Efetuando o login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == botaoLogin){
            loginUser();
        }
        else if(view == tvCadastro){
            finish();
            startActivity(new Intent(this,CadastroActivity.class));
        }
    }
}
