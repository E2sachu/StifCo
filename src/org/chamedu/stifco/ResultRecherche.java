package org.chamedu.stifco;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ResultRecherche extends Activity {
	
	private String json;
	JSONObject jsonResponse;
	JSONArray arrayJson;
	ArrayList<Propositions> totalProp = new ArrayList<Propositions>();
	
	ListView resultSearch;
	PropositionsAdapter adapter = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
		resultSearch = (ListView)findViewById(R.id.listPropositions);
		json = (String) getIntent().getExtras().get("liste");

		try {
			jsonResponse = new JSONObject(json);
			// Cr�ation du tableau g�n�ral �partir d'un JSONObject
			JSONArray jsonArray = jsonResponse.getJSONArray("propositions");
			Propositions proposals = null;
			
			// Pour chaque �l�ment du tableau
			for (int i = 0; i < jsonArray.length(); i++) {
				proposals = new Propositions();
				// Cr�ation d'un tableau �l�ment � partir d'un JSONObject
				JSONObject jsonObj = jsonArray.getJSONObject(i);

				// R�cup�ration �partir d'un JSONObject nomm�
				//JSONObject fields  = jsonObj.getJSONObject("fields");

				// R�cup�ration de l'item qui nous int�resse
				//String nom = fields.getString("nom_de_la_gare");
				
				proposals.setId(jsonObj.getString("id"));
				proposals.setVille(jsonObj.getString("ville"));
				proposals.setLieu(jsonObj.getString("lieu"));

				// Ajout dans l'ArrayList
				totalProp.add(proposals);	
				//Log.i("item "+i, ""+horaire);
			}
			adapter = new PropositionsAdapter(this, totalProp);
			
			resultSearch.setAdapter(adapter);
		}catch (JSONException e) {
			e.printStackTrace();
		}

		//}

		
	}	

	
	
	
	
}
