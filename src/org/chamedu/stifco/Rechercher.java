package org.chamedu.stifco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import network.OnResultListener;
import network.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Rechercher extends Activity implements OnClickListener, OnItemSelectedListener {
	
	Button rechercher;
	Spinner mois;
	
	private String moisSelect;
	// Varaibles pour la lecture du flux Json
		private String jsonString;
		JSONObject jsonResponse;
		JSONArray arrayJson;
		AutoCompleteTextView tvGareAuto;
		ArrayList<String> items = new ArrayList<String>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rechercher);
		
		rechercher = (Button)findViewById(R.id.search);
		rechercher.setOnClickListener(this);
		
		// Traitement du textView en autocomplétion à  partir de la source Json
				jsonString = lireJSON();

				try {
					jsonResponse = new JSONObject(jsonString);
					// Création du tableau général à partir d'un JSONObject
					JSONArray jsonArray = jsonResponse.getJSONArray("gares");

					// Pour chaque élément du tableau
					for (int i = 0; i < jsonArray.length(); i++) {

						// Création d'un tableau élément à  partir d'un JSONObject
						JSONObject jsonObj = jsonArray.getJSONObject(i);

						// Récupération à partir d'un JSONObject nommé
						JSONObject fields  = jsonObj.getJSONObject("fields");

						// Récupération de l'item qui nous intéresse
						String nom = fields.getString("nom_de_la_gare");

						// Ajout dans l'ArrayList
						items.add(nom);		
					}

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, items);
					tvGareAuto = (AutoCompleteTextView)findViewById(R.id.gare);
					tvGareAuto.setAdapter(adapter);

				} catch (JSONException e) {
					e.printStackTrace();
				}
		
		
		mois = (Spinner) findViewById(R.id.spinnerMois);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.months_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mois.setAdapter(adapter);
		mois.setOnItemSelectedListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		if ( v == rechercher ) {
			Toast.makeText(Rechercher.this, "Recherche en cours", Toast.LENGTH_LONG).show();
			if(!tvGareAuto.getText().equals("")){
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				Log.i("gare", ""+tvGareAuto.getText());
				Log.i("mois",moisSelect);
	
				nameValuePairs.add(new BasicNameValuePair("gare",""+tvGareAuto.getText()));
				nameValuePairs.add(new BasicNameValuePair("mois",moisSelect.toLowerCase()));
				
				try {				
					RestClient.doPost("/recherche.php", nameValuePairs, new OnResultListener() {					
						@Override
						public void onResult(String json) {
							Log.i("resultat", ""+json);
							if ( !json.equals("recherche_vide")) {
								Intent iResultatRecherche = new Intent(getBaseContext(), ResultRecherche.class);
								iResultatRecherche.putExtra("liste", json);
								startActivityForResult( iResultatRecherche,10 );
							} else {
								Toast.makeText(Rechercher.this, "Aucune proposition n\'a ï¿½tï¿½ faite.", Toast.LENGTH_LONG).show();
							}					
						}
					});
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	public String lireJSON() {
		InputStream is = getResources().openRawResource(R.raw.gares);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writer.toString();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		moisSelect = (String) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}




