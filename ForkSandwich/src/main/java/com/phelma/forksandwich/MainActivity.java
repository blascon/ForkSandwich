package com.phelma.forksandwich;
/*
Il y a pas mal d'import inutiles, j'ai commenté ceux qui ne servent pas.
 */
import java.io.BufferedReader;
//  import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//  import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
//  import java.sql.Date;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
//  import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
//  import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    /*
    Tout ça, la plupart ne sert à rien. Mais bon, ce n'est pas le problème :)
     */
    //private static final int PROGRESS = 0x1;
    Button calcul,show;
    TextView mymatf,mymatc,results;
    EditText tmin,tmax,dt,cmin,cmax,dc,mmin,mmax,rigmin,rigmax,mytime;
    TextView textt,textc,textm,texts;
    double vtmin,vtmax,vdt,vcmin,vcmax,vdc,vmmin,vmmax,vrigmin,vrigmax;
    Chronometer chrono;
    long time=0;
    ProgressDialog progress;
    int progressStatus;
    //int progressBarStatus;
    private Handler progressBarHandler = new Handler();

    /*
    J'ai regroupé les variables de même famille, soucis de lisibilité.
     */
    int nbmatf,nbmatc;
    String[] materialf;
    Double[] rhominf,rhomaxf,Eminf,Emaxf,sigminf,sigmaxf,numinf,numaxf,condminf,condmaxf;
    Double[] rhomoyf,Emoyf,sigmoyf,numoyf,condmoyf;
    String[] materialc;
    Double[] rhominc,rhomaxc,Eminc,Emaxc,sigminc,sigmaxc,numinc,numaxc,condminc,condmaxc;
    Double[] rhomoyc,Emoyc,sigmoyc,numoyc,condmoyc;
    String[] solution;

    int nbcase, nbsol;

    /*
    Fonction inutilisée. Je la laisse en attendant de savoir à quoi elle sert.
     */
    void lecturef(String name)
    {
        AssetManager mngr;
        String line = null;
        Charset charset = Charset.forName("ISO_8859_1");
        CharsetDecoder charsetDecoder = charset.newDecoder();

        try{
            mngr= getAssets();

            InputStream is = mngr.open(name);
            InputStreamReader isr = new InputStreamReader(is, charsetDecoder);
            BufferedReader br = new BufferedReader(isr);
            line=br.readLine();
            nbmatf=(new Integer(line)).intValue();
            Eminf=new Double[nbmatf];
            Emaxf=new Double[nbmatf];
            Emoyf=new Double[nbmatf];
            rhominf=new Double[nbmatf];
            rhomaxf=new Double[nbmatf];
            rhomoyf=new Double[nbmatf];
            materialf=new String[nbmatf];
            int nb=0;
            //br.readLine(); // Ligne de commentaires
            while((line=br.readLine())!=null){
                StringTokenizer t = new StringTokenizer(line,";");
                materialf[nb]=t.nextToken();
                Eminf[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emaxf[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emoyf[nb]=0.5*(Eminf[nb]+Emaxf[nb]);
                rhominf[nb]= (new Double(t.nextToken())).doubleValue();
                rhomaxf[nb]= (new Double(t.nextToken())).doubleValue();
                rhomoyf[nb]=0.5*(rhominf[nb]+rhomaxf[nb]);
                nb=nb+1;
            }
            //Toast.makeText(this,"nb materials : "+nbmat, Toast.LENGTH_SHORT).show();

            br.close();
        }catch(IOException e1){
            Toast.makeText(this,"pb in lecture ", Toast.LENGTH_SHORT).show();

        }
    }

    void lecturec(String name)
    {
        AssetManager mngr;
        String line = null;
        Charset charset = Charset.forName("ISO_8859_1");
        CharsetDecoder charsetDecoder = charset.newDecoder();

        try{
            mngr= getAssets();


            InputStream is = mngr.open(name);
            InputStreamReader isr = new InputStreamReader(is, charsetDecoder);
            BufferedReader br = new BufferedReader(isr);
            line=br.readLine();
            nbmatc=(new Integer(line)).intValue();
            Eminc=new Double[nbmatc];
            Emaxc=new Double[nbmatc];
            Emoyc=new Double[nbmatc];
            rhominc=new Double[nbmatc];
            rhomaxc=new Double[nbmatc];
            rhomoyc=new Double[nbmatc];
            materialc=new String[nbmatc];
            int nb=0;
            while((line=br.readLine())!=null){
                StringTokenizer t = new StringTokenizer(line,";");
                materialc[nb]=t.nextToken();
                Eminc[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emaxc[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emoyc[nb]=0.5*(Eminc[nb]+Emaxc[nb]);
                rhominc[nb]= (new Double(t.nextToken())).doubleValue();
                rhomaxc[nb]= (new Double(t.nextToken())).doubleValue();
                rhomoyc[nb]=0.5*(rhominc[nb]+rhomaxc[nb]);
                nb=nb+1;
            }
            //Toast.makeText(this,"nb materials : "+nbmat, Toast.LENGTH_SHORT).show();

            br.close();
        }catch(IOException e1){
            Toast.makeText(this,"pb in lecture ", Toast.LENGTH_SHORT).show();

        }
    }

    /*
    Fonction que j'ai rajouté histoire de tester l'accès aux fichiers. N'est plus utilisée, à part
    pour comprendre le fonctionnement des assets.
     */
    void test_lecture(String name)
    {
        AssetManager mngr;
        Charset charset = Charset.forName("ISO_8859_1");
        CharsetDecoder charsetDecoder = charset.newDecoder();
        String[] files = null;

        try {
            mngr= getAssets();
            files = mngr.list("");
        } catch (IOException e) {
               Toast.makeText(this,e.getMessage() + " : Erreur de lecture", Toast.LENGTH_LONG).show();
            }
        Toast.makeText(this,files[0] + " Lu", Toast.LENGTH_LONG).show();
    }

    /*
    Et c'est parti ! Fonction utilisée pour lire face.txt. Enregistre dans des tableaux.
     */

    void lecture_face(String name)
    {
        AssetManager mngr;
        String line;
        Charset charset = Charset.forName("ISO_8859_1");
        CharsetDecoder charsetDecoder = charset.newDecoder();

            try{                    // Permet de retrouver un code d'erreur, sans faire planter l'appli.
                mngr= getAssets();

            InputStream is = mngr.open(name);
            InputStreamReader isr = new InputStreamReader(is, charsetDecoder);
            BufferedReader br = new BufferedReader(isr);
            line=br.readLine();
            nbmatf=(new Integer(line)).intValue();
            Eminf=new Double[nbmatf];   Emaxf=new Double[nbmatf];   Emoyf=new Double[nbmatf];
            sigminf=new Double[nbmatf]; sigmaxf=new Double[nbmatf]; sigmoyf=new Double[nbmatf];
            rhominf=new Double[nbmatf]; rhomaxf=new Double[nbmatf]; rhomoyf=new Double[nbmatf];
            numinf=new Double[nbmatf];  numaxf=new Double[nbmatf];  numoyf=new Double[nbmatf];
            condminf=new Double[nbmatf];condmaxf=new Double[nbmatf];condmoyf=new Double[nbmatf];
            materialf=new String[nbmatf];
            int nb=0;
            while((line=br.readLine())!=null){  // Lecture. Ressemble à ce qu'on fait en C, avec un séparateur de champs.
                StringTokenizer t = new StringTokenizer(line,";");
                Eminf[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emaxf[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emoyf[nb]=0.5*(Eminf[nb]+Emaxf[nb]);
                rhominf[nb]= 1e3*(new Double(t.nextToken())).doubleValue();
                rhomaxf[nb]= 1e3*(new Double(t.nextToken())).doubleValue();
                rhomoyf[nb]=0.5*(rhominf[nb]+rhomaxf[nb]);
                sigminf[nb]= 1e6*(new Double(t.nextToken())).doubleValue();
                sigmaxf[nb]= 1e6*(new Double(t.nextToken())).doubleValue();
                sigmoyf[nb]=0.5*(sigminf[nb]+sigmaxf[nb]);
                numinf[nb]= (new Double(t.nextToken())).doubleValue();
                numaxf[nb]= (new Double(t.nextToken())).doubleValue();
                numoyf[nb]=0.5*(numinf[nb]+numaxf[nb]);
                condminf[nb]= (new Double(t.nextToken())).doubleValue();
                condmaxf[nb]= (new Double(t.nextToken())).doubleValue();
                condmoyf[nb]=0.5*(condminf[nb]+condmaxf[nb]);
                materialf[nb]=t.nextToken();
                nb=nb+1;
            }
            //Toast.makeText(this,"nb materials : "+nbmat, Toast.LENGTH_SHORT).show();
            br.close();
        }catch(IOException e1){
        Toast.makeText(this,"pb in lecture ", Toast.LENGTH_SHORT).show();

        }
    }

    /*
    Fichier core.txt
     */
    void lecture_core(String name)
    {
        AssetManager mngr;
        String line = null;
        Charset charset = Charset.forName("ISO_8859_1");
        CharsetDecoder charsetDecoder = charset.newDecoder();

        try{
            mngr= getAssets();

            InputStream is = mngr.open(name);
            InputStreamReader isr = new InputStreamReader(is, charsetDecoder);
            BufferedReader br = new BufferedReader(isr);
            line=br.readLine();
                nbmatc=(new Integer(line)).intValue();
            Eminc=new Double[nbmatc];   Emaxc=new Double[nbmatc];   Emoyc=new Double[nbmatc];
            sigminc=new Double[nbmatc]; sigmaxc=new Double[nbmatc]; sigmoyc=new Double[nbmatc];
            rhominc=new Double[nbmatc]; rhomaxc=new Double[nbmatc]; rhomoyc=new Double[nbmatc];
            numinc=new Double[nbmatc];  numaxc=new Double[nbmatc];  numoyc=new Double[nbmatc];
            condminc=new Double[nbmatc];condmaxc=new Double[nbmatc];condmoyc=new Double[nbmatc];
            materialc=new String[nbmatc];

            int nb=0;
            while((line=br.readLine())!=null){
                StringTokenizer t = new StringTokenizer(line,";");
                Eminc[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emaxc[nb]= 1e9*(new Double(t.nextToken())).doubleValue();
                Emoyc[nb]=0.5*(Eminc[nb]+Emaxc[nb]);
                rhominc[nb]= 1e3*(new Double(t.nextToken())).doubleValue();
                rhomaxc[nb]= 1e3*(new Double(t.nextToken())).doubleValue();
                rhomoyc[nb]=0.5*(rhominc[nb]+rhomaxc[nb]);
                sigminc[nb]= 1e6*(new Double(t.nextToken())).doubleValue();
                sigmaxc[nb]= 1e6*(new Double(t.nextToken())).doubleValue();
                sigmoyc[nb]=0.5*(sigminc[nb]+sigmaxc[nb]);
                numinc[nb]= (new Double(t.nextToken())).doubleValue();
                numaxc[nb]= (new Double(t.nextToken())).doubleValue();
                numoyc[nb]=0.5*(numinc[nb]+numaxc[nb]);
                condminc[nb]= (new Double(t.nextToken())).doubleValue();
                condmaxc[nb]= (new Double(t.nextToken())).doubleValue();
                condmoyc[nb]=0.5*(condminc[nb]+condmaxc[nb]);
                materialc[nb]=t.nextToken();
                nb=nb+1;
            }

            //Toast.makeText(this,"nb materials : "+nbmat, Toast.LENGTH_SHORT).show();
            br.close();
        }catch(IOException e1){
            Toast.makeText(this,"pb in lecture "+e1, Toast.LENGTH_SHORT).show();

        }
    }

    /*
    Calcul des valeurs utiles. Si on compare aux datas du logiciel CES, on retrouve plus ou moins
    les mêmes trucs. Cool.
     */

    void calcul(double tmin, double tmax, double dt,
                double cmin, double cmax, double dc,
                double mmin, double mmax,
                double rigmin, double rigmax)
    {
        //solution=new String[100000];

        nbcase=0;
        nbsol=0;
        for(int i=0;i<nbmatf;i++){
        for(int j=0;j<nbmatc;j++){
        for(double t=tmin;t<=tmax;t=t+dt){
        for(double c=cmin;c<=cmax;c=c+dc){
            double sand_mass=(2*rhomoyf[i]*t*1e-3+rhomoyc[j]*c*1e-3);
            double L=1;
            double b=0.05;
            double d=c+t;
            double rig1=L*L*L/(24*Emoyf[i]*t*d*d);
            double rig2=L*t/(4*Emoyc[i]*b*d*d/2.6);
            double sand_rig=1/(rig1+rig2);
            nbcase=nbcase+1;
            if ( (sand_mass<=mmax && sand_mass>=mmin) &&(sand_rig<=rigmax && sand_rig>=rigmin)) {
                nbsol=nbsol+1;
                results.setText("nb sol ="+nbsol+ "/"+nbcase);
                // C'est ma que je ne comprends pas : Pourquoi retourner CES valeurs qui ne servent à rien ?
                //solution[nbsol] = materialf[i] +";" + materialc[j] +";" + t  +";" + c +";" + sand_mass +";" + sand_rig;
            }
        }
        }
        }
        }
    }

    /*
    Récupération des valeurs depuis les champs.
     */
    void getvalues()
    {
       try{
           vtmin = Double.parseDouble(tmin.getText().toString());
           } catch (final NumberFormatException e) {
            vtmin = 1.0;
            tmin.setText(""+vtmin);
           }  
       try{
           vtmax = Double.parseDouble(tmax.getText().toString());
           } catch (final NumberFormatException e) {
            vtmax = 2.0;
            tmax.setText(""+vtmax);
        }
        try{
            vdt = Double.parseDouble(dt.getText().toString());
        } catch (final NumberFormatException e) {
            vdt = 0.1;
            dt.setText(""+vdt);
        }
        try{
            vcmin = Double.parseDouble(cmin.getText().toString());
        } catch (final NumberFormatException e) {
            vcmin = 5.0;
            cmin.setText(""+vcmin);
        }
        try{
            vcmax = Double.parseDouble(cmax.getText().toString());
        } catch (final NumberFormatException e) {
            vcmax = 20.0;
            cmax.setText(""+vtmax);
        }
        try{
            vdc = Double.parseDouble(dc.getText().toString());
        } catch (final NumberFormatException e) {
            vdc = 0.1;
            dc.setText(""+vdc);
        }
        try{
            vmmin = Double.parseDouble(mmin.getText().toString());
        } catch (final NumberFormatException e) {
            vmmin = 0.0;
            mmin.setText(""+vmmin);
        }
        try{
            vmmax = Double.parseDouble(mmax.getText().toString());
        } catch (final NumberFormatException e) {
            vmmax = 20.0;
            mmax.setText(""+vcmax);
        }
        try{
            vrigmin = Double.parseDouble(rigmin.getText().toString());
        } catch (final NumberFormatException e) {
            vrigmin = 1e-6;
            rigmin.setText(""+vrigmin);
        }
        try{
            vrigmax = Double.parseDouble(rigmax.getText().toString());
        } catch (final NumberFormatException e) {
            vrigmax = 5e-6;
            rigmax.setText(""+vrigmax);
        }

    }

    void initializevalues()
    {
        vtmin = 1.0;
        tmin.setText(""+vtmin);
        vtmax = 2.0;
        tmax.setText(""+vtmax);
        vdt = 0.1;
        dt.setText(""+vdt);
        vcmin = 10.0;
        cmin.setText(""+vcmin);
        vcmax = 20.0;
        cmax.setText(""+vcmax);
        vdc = 1.0;
        dc.setText(""+vdc);
        vmmin = 0.0;
        mmin.setText(""+vmmin);
        vmmax = 20.0;
        mmax.setText(""+vmmax);
        vrigmin = 1e5;
        rigmin.setText(""+vrigmin);
        vrigmax = 1e10;
        rigmax.setText(""+vrigmax);

    }

    /*
    Fonction globale.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calcul=(Button)findViewById(R.id.calcul);
        mymatf=(TextView)findViewById(R.id.matf);
        mymatc=(TextView)findViewById(R.id.matc);
        textt=(TextView)findViewById(R.id.tt);
        textc=(TextView)findViewById(R.id.cc);
        textm=(TextView)findViewById(R.id.mm);
        texts=(TextView)findViewById(R.id.ss);
        tmin=(EditText)findViewById(R.id.tmin);
        tmax=(EditText)findViewById(R.id.tmax);
        dt=(EditText)findViewById(R.id.inct);
        cmin=(EditText)findViewById(R.id.cmin);
        cmax=(EditText)findViewById(R.id.cmax);
        dc=(EditText)findViewById(R.id.incc);
        mmin=(EditText)findViewById(R.id.mmin);
        mmax=(EditText)findViewById(R.id.mmax);
        mytime=(EditText)findViewById(R.id.tictoc);
        rigmin=(EditText)findViewById(R.id.stiffmin);
        rigmax=(EditText)findViewById(R.id.stiffmax);
        results=(TextView)findViewById(R.id.result);
        chrono=(Chronometer)findViewById(R.id.chronometer);


        results.setText("");
//        lecturef("faces.txt");        // C'était déjà commenté… Je sais pas pourquoi…
//           lecturec("foam.txt");      // Idem
        lecture_face("face.txt");
        lecture_core("core.txt");
        //test_lecture("assets/core.txt");
        mymatf.setText("nb of face materials : "+nbmatf);
        mymatc.setText("nb of core materials : "+nbmatc);

        initializevalues();



        // create click listener
        calcul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nbcase=0;
                nbsol=0;
                results.setText("Computing ....");
                calcul(vtmin,vtmax,vdt,vcmin,vcmax,vdc,vmmin,vmmax,vrigmin,vrigmax);
                results.setText("nb sol ="+nbsol+ "/"+nbcase);

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}