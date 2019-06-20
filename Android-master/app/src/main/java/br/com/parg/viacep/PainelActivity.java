package br.com.parg.viacep;

//import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import viacep.ViaCEP;

public class PainelActivity extends AppCompatActivity implements Button.OnClickListener {
    private Button btnBuscar;
    private EditText txtCEP;
    private EditText txtLogradouro;
    private EditText txtComplemento;
    private EditText txtBairro;
    private EditText txtLocalidade;
    private EditText txtUf;
    private EditText txtIbge;
    private EditText txtGia;
    private ViaCEP vCEP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Criado por Pablo Alexander da Rocha Gonçalves", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // define
        //this.vCEP = null;

        // referência
        this.btnBuscar = (Button) findViewById(R.id.btnBuscar);
        this.txtCEP = (EditText) findViewById(R.id.txtCEP);
        this.txtLogradouro = (EditText) findViewById(R.id.txtLogradouro);
        this.txtComplemento = (EditText) findViewById(R.id.txtComplemento);
        this.txtBairro = (EditText) findViewById(R.id.txtBairro);
        this.txtLocalidade = (EditText) findViewById(R.id.txtLocalidade);
        this.txtUf = (EditText) findViewById(R.id.txtUf);
        this.txtIbge = (EditText) findViewById(R.id.txtIbge);
        this.txtGia = (EditText) findViewById(R.id.txtGia);

        // cria a máscara
        MaskEditTextChangedListener maskCEP = new MaskEditTextChangedListener("#####-###", this.txtCEP);

        // adiciona a máscara no objeto
        this.txtCEP.addTextChangedListener(maskCEP);

        // define o evento de clique
        this.btnBuscar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // evento para buscar um cep
        if (view == this.btnBuscar) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // limpa
                this.txtBairro.setText("");
                this.txtComplemento.setText("");
                this.txtGia.setText("");
                this.txtIbge.setText("");
                this.txtLocalidade.setText("");
                this.txtLogradouro.setText("");
                this.txtUf.setText("");

                // cep
                String cep = this.txtCEP.getText().toString();

                // verifica se o CEP é válido
                Pattern pattern = Pattern.compile("^[0-9]{5}-[0-9]{3}$");
                Matcher matcher = pattern.matcher(cep);

                if (matcher.find()) {
                    new DownloadCEPTask().execute(cep);
                } else {
                    //JOptionPane.showMessageDialog(null, "Favor informar um CEP válido!", "Aviso!", JOptionPane.WARNING_MESSAGE);
                    new AlertDialog.Builder(this)
                            .setTitle("Aviso!")
                            .setMessage("Favor informar um CEP válido!")
                            .setPositiveButton(R.string.msgOk, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // nada
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Sem Internet!")
                        .setMessage("Não tem nenhuma conexão de rede disponível!")
                        .setPositiveButton(R.string.msgOk, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // nada
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_painel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadCEPTask extends AsyncTask<String, Void, ViaCEP> {
        @Override
        protected ViaCEP doInBackground(String ... ceps) {
            ViaCEP vCep = null;

            try {
                vCep = new ViaCEP(ceps[0]);
            } finally {
                return vCep;
            }
        }

        @Override
        protected void onPostExecute(ViaCEP result) {
            if (result != null) {
                txtBairro.setText(result.getBairro());
                txtComplemento.setText(result.getComplemento());
                txtGia.setText(result.getGia());
                txtIbge.setText(result.getIbge());
                txtLocalidade.setText(result.getLocalidade());
                txtLogradouro.setText(result.getLogradouro());
                txtUf.setText(result.getUf());
            }
        }
    }
}
