package br.ufc.tele_diabetes.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.ufc.tele_diabetes.R;

/**
 * A login screen that offers login via email/password.
 */
public class CadastroActivity extends Activity implements OnClickListener {

    private Button botaoCadastro;
    private EditText edEmail, edSenha;
    private TextView tvLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        botaoCadastro = (Button)findViewById(R.id.botaoCadastro);
        edEmail = (EditText)findViewById(R.id.email);
        edSenha = (EditText)findViewById(R.id.password);
        tvLogin = (TextView)findViewById(R.id.facaLogin);

        botaoCadastro.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

    }

    private void registrarUser(){
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

        // Validação de email

        progressDialog.setMessage("Registrando o usuário...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            Toast.makeText(CadastroActivity.this, "Usuário registrado", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        }else{
                            Toast.makeText(CadastroActivity.this, "Erro ao registrar o usuário...por favor, tente novamente", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == botaoCadastro){
            registrarUser();
        }
        else if(view == tvLogin){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }
}

