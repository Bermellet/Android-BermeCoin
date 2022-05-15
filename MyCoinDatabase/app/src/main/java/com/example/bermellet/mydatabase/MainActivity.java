package com.example.bermellet.mydatabase;


import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    // Variables Globales
    private CoinData coinData;
    private CustomAdapter adapter;
    private ListView listView;
    private List<Coin> llistaCoins;
    private int CARREGA_IMG1 = 1;
    private int CARREGA_IMG2 = 2;
    private ImageView selectImg1;
    private ImageView selectImg2;
    String path1,path2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Inicialitzacions
        // CoinData
        coinData = new CoinData(this);
        coinData.open();
        llistaCoins = coinData.getAllCoins();
        // Adapter
        adapter = new CustomAdapter(getApplicationContext(), llistaCoins);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);   // Menu flotant

        // Floating Button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAfegir(view);
            /*Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
            }
        });



        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                System.out.println("long clicked, pos: "+i);
                return true;
            }
        });*/
    }

    // Basic method to add pseudo-random list of books so that
    // you have an example of insertion and deletion

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    /*public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Coin> adapter = (ArrayAdapter<Coin>) getListAdapter();
        Coin coin;
        switch (view.getId()) {
            case R.id.add:
                String[] newCoins = new String[] { "Euro", "Dòlar", "Ien", "Lliura esterlina"};
                double[] newValues = new double[] { 0.01, 0.05, 0.10, 0.50, 1 };

                int c = new Random().nextInt(4);
                int v = new Random().nextInt(5);

                // save the new book to the database
                if (c == 2) {
                    coin = coinData.createCoin(newCoins[c], newValues[v]*100);
                }
                else {
                    coin = coinData.createCoin(newCoins[c], newValues[v]);
                }

                // After I get the book data, I add it to the adapter
                adapter.add(coin);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    coin = (Coin) getListAdapter().getItem(0);
                    coinData.deleteCoin(coin);
                    adapter.remove(coin);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }*/

    // Menu de la TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_header, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AlertDialog dialog_help = new AlertDialog.Builder(this)
                    .setTitle("Ajuda")
                    .setMessage("Senzilla app per administrar les teves monedes de manera còmoda i senzilla\n\n" +
                            "\t- El botó inferior dret serveix per afegir noves monedes\n\n" +
                            "\t- Mantenir pressionada una moneda afegida per poder-la Editar/Eliminar")
                    .setPositiveButton("D'acord",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
                dialog_help.show();
                return true;
            case R.id.action_about:
                AlertDialog dialog_about = new AlertDialog.Builder(this)
                    .setTitle("Sobre aquesta app")
                    .setMessage("App propietat de Bermellet \u00a9 2017\n\n" +
                            "Prohibida la seva venta o distribució sense el permís explícit del seu creador.")
                    .setPositiveButton("D'acord",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
                dialog_about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // FLOATING BUTTON Afegir

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_flotante,menu);

        super.onCreateContextMenu(menu,v,menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.menu_editar:
                // Toast.makeText(getApplicationContext(), "Editar "+ info.id, Toast.LENGTH_SHORT).show();
                editarMoneda((int)info.id);
                break;
            case R.id.menu_eliminar:
                //Toast.makeText(getApplicationContext(), "Eliminar "+ info.id, Toast.LENGTH_SHORT).show();
                eliminarMoneda((int)info.id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    // CLASE CUSTOM ADAPTER
    private class CustomAdapter extends BaseAdapter {
        private Context context;
        private List<Coin> llista;

        // Funciones de la clase
        public CustomAdapter(Context context, List<Coin> llista){
            this.context = context;
            this.llista = llista;
        }

        @Override
        public int getCount(){
            return llista.size();
        }

        @Override
        public Coin getItem(int pos){
            return llista.get(pos);
        }

        @Override
        public long getItemId(int pos){
            return pos;
        }


        @Override
        public View getView(int pos, View convertView, ViewGroup parent){
            convertView = getLayoutInflater().inflate(R.layout.custom_layout, null);
            // Obtenim els objectes del Layout
            TextView coinNom = (TextView) convertView.findViewById(R.id.textNom);
            TextView coinValor = (TextView) convertView.findViewById(R.id.textValor);
            TextView coinAny = (TextView) convertView.findViewById(R.id.textAny);
            TextView coinPais = (TextView) convertView.findViewById(R.id.textPais);
            TextView coinDesc = (TextView) convertView.findViewById(R.id.textDescripcio);
            ImageView img1 = (ImageView) convertView.findViewById(R.id.imageView1);
            ImageView img2 = (ImageView) convertView.findViewById(R.id.imageView2);
            // Apliquem el valor als objectes
            coinNom.setText(llista.get(pos).getCurrency());
            coinValor.setText(String.valueOf(llista.get(pos).getValue()));
            coinAny.setText(String.valueOf(llista.get(pos).getYear()));
            coinPais.setText(llista.get(pos).getCountry());
            coinDesc.setText(llista.get(pos).getDescription());
            String path1 = llista.get(pos).getPath1(), path2 = llista.get(pos).getPath2();
            if (path1 != null)
                img1.setImageDrawable(Drawable.createFromPath(path1));
            if (path2 != null)
                img2.setImageDrawable(Drawable.createFromPath(llista.get(pos).getPath2()));

            return convertView;
        }

        private void refresh() {
            llista = coinData.getAllCoins();
            adapter.notifyDataSetChanged();
        }

    }

    // Editar/Afegir Moneda

    public void eliminarMoneda(int pos) {
        final Coin coin = adapter.getItem(pos);

        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Eliminant moneda \""+ coin.getCurrency() + "\"")
            .setMessage("Segur que vols eliminar la moneda?\nAquest pas no es pot rectificar")
            .setNegativeButton("Eliminar", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    coinData.deleteCoin(coin);
                    adapter.refresh();
                    Toast.makeText(MainActivity.this, "Eliminat Correctament", Toast.LENGTH_SHORT).show();

                }

            })
            .setPositiveButton("Cancel·la", null)
            .show();
    }

    public void editarMoneda(int pos) {
        //Toast.makeText(getApplicationContext(), "Funcionalitat en DESENVOLUPAMENT", Toast.LENGTH_SHORT).show();
        // Inicialitzem Variables
        final Coin coin = adapter.getItem(pos);
        path1 = coin.getPath1();
        path2 = coin.getPath2();

        // Creem el Layout per demanar dades
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        InputFilter[] filter = new InputFilter[1]; // Filtres de longitud a la entrada

        // Creem els inputs amb els tipus d'entrada esperats

        final EditText inputDivisa = new EditText(this);
        inputDivisa.setText(coin.getCurrency());
        inputDivisa.setFocusable(false);
        inputDivisa.setClickable(false);
        inputDivisa.setEnabled(false);
        layout.addView(inputDivisa);

        final EditText inputValor = new EditText(this);
        inputValor.setText(String.valueOf(coin.getValue()));
        inputValor.setFocusable(false);
        inputValor.setClickable(false);
        inputValor.setEnabled(false);
        layout.addView(inputValor);

        final EditText inputPais = new EditText(this);
        inputPais.setText(coin.getCountry());
        inputPais.setFocusable(false);
        inputPais.setClickable(false);
        inputPais.setEnabled(false);
        layout.addView(inputPais);

        final EditText inputAny = new EditText(this);
        inputAny.setHint("Any");
        inputAny.setText(String.valueOf(coin.getYear()));
        inputAny.setInputType(InputType.TYPE_CLASS_NUMBER);
        filter[0] = new InputFilter.LengthFilter(4);
        inputAny.setFilters(filter);
        layout.addView(inputAny);

        final EditText inputDescripcio = new EditText(this);
        inputDescripcio.setHint("Descripció");
        inputDescripcio.setText(coin.getDescription());
        inputDescripcio.setInputType(InputType.TYPE_CLASS_TEXT);
        filter[0] = new InputFilter.LengthFilter(90);
        inputDescripcio.setFilters(filter);
        layout.addView(inputDescripcio);

        // Imatge
        selectImg1 = new ImageView(this);
        selectImg1.setMinimumHeight(150);
        selectImg1.setMinimumWidth(150);
        selectImg1.setMaxHeight(150);
        selectImg1.setMaxWidth(150);
        if (path1 != null) selectImg1.setImageDrawable(Drawable.createFromPath(path1));
        else selectImg1.setImageResource(android.R.drawable.ic_delete);
        selectImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CARREGA_IMG1);
            }
        });

        /////////////////////////////////////////

        selectImg2 = new ImageView(this);
        selectImg2.setMinimumHeight(150);
        selectImg2.setMinimumWidth(150);
        selectImg2.setMaxHeight(150);
        selectImg2.setMaxWidth(150);
        if (path2 != null) selectImg2.setImageDrawable(Drawable.createFromPath(path2));
        else selectImg2.setImageResource(android.R.drawable.ic_delete);
        selectImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CARREGA_IMG2);
            }
        });
        //layout.addView(selectImg2);

        // Botons d'esborrat
        Button clear1 = new Button (this);
        clear1.setText("Esborra IMG1");
        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path1 = null;
                selectImg1.setImageResource(android.R.drawable.ic_delete);
            }
        });

        Button clear2 = new Button (this);
        clear2.setText("Esborra IMG2");
        clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path2 = null;
                selectImg2.setImageResource(android.R.drawable.ic_delete);
            }
        });

        LinearLayout layImgs = new LinearLayout(this);
        layImgs.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout layElem = new LinearLayout(this);
        layElem.setOrientation(LinearLayout.VERTICAL);
        layElem.addView(selectImg1);
        layElem.addView(selectImg2);
        layElem.setMinimumWidth(500);
        layImgs.addView(layElem);

        layElem = new LinearLayout(this);
        layElem.setOrientation(LinearLayout.VERTICAL);
        layElem.addView(clear1);
        layElem.addView(clear2);
        layImgs.addView(layElem);
        layout.addView(layImgs);

        // Creem AlertDialog que demanarà dades
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Modificar Moneda \""+ coin.getCurrency() + "\"")
                .setPositiveButton("Cancel·la", null) // Canviem ordre del botons per tenir Cancel·lar a la dreta (ICS)
                .setNegativeButton("Actualitza", null)
                .create();

        // Sobreescrivim el botó perque no es tanqui al acceptar si falten camps
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!inputAny.getText().toString().isEmpty() && !inputDescripcio.getText().toString().isEmpty()) {
                            // Modifiquem la Moneda
                            coinData.modifyCoin(coin.getId(),coin.getCurrency(),coin.getValue(),Integer.parseInt(inputAny.getText().toString()),
                                    coin.getCountry(), inputDescripcio.getText().toString(), path1, path2);
                            // Refresquem el ArrayAdapter
                            adapter.refresh();
                            // Avisem que tot és correcte
                            Toast.makeText(MainActivity.this, "Modificat Correctament", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(MainActivity.this, "Has d'omplir tots els camps!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button buttonNegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        // Mostrem el dialeg
        dialog.show();
    }


    // FUNCIONS MEVES
    public static String getRealPathFromURI_WTF (Context context, Uri uri) {
        String filePath = "";
        try {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column,sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return filePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data && data.getData() != null) {
            if (requestCode == CARREGA_IMG1) {
                path1 = getRealPathFromURI_WTF(this, data.getData());
                selectImg1.setImageDrawable(Drawable.createFromPath(path1));
            } else if (requestCode == CARREGA_IMG2) {
                path2 = getRealPathFromURI_WTF(this, data.getData());
                selectImg2.setImageDrawable(Drawable.createFromPath(path2));
            }
        }
    }


    public void onClickAfegir(View view) {
        // Inicialitzem Variables
        path1 = path2 = null;

        // Creem el Layout per demanar dades
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        InputFilter[] filter = new InputFilter[1]; // Filtres de longitud a la entrada

        // Creem els inputs amb els tipus d'entrada esperats
        final EditText inputDivisa = new EditText(this);
        inputDivisa.setHint("Nom Divisa");
        inputDivisa.setInputType(InputType.TYPE_CLASS_TEXT);
        filter[0] = new InputFilter.LengthFilter(20);
        inputDivisa.setFilters(filter);
        layout.addView(inputDivisa);

        final EditText inputValor = new EditText(this);
        inputValor.setHint("Valor");
        inputValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputValor);

        final EditText inputPais = new EditText(this);
        inputPais.setHint("País");
        inputPais.setInputType(InputType.TYPE_CLASS_TEXT);
        filter[0] = new InputFilter.LengthFilter(12);
        inputPais.setFilters(filter);
        layout.addView(inputPais);

        final EditText inputAny = new EditText(this);
        inputAny.setHint("Any");
        inputAny.setInputType(InputType.TYPE_CLASS_NUMBER);
        filter[0] = new InputFilter.LengthFilter(4);
        inputAny.setFilters(filter);
        layout.addView(inputAny);

        final EditText inputDescripcio = new EditText(this);
        inputDescripcio.setHint("Descripció");
        inputDescripcio.setInputType(InputType.TYPE_CLASS_TEXT);
        filter[0] = new InputFilter.LengthFilter(90);
        inputDescripcio.setFilters(filter);
        layout.addView(inputDescripcio);

        // Imatge
        selectImg1 = new ImageView(this);
        selectImg1.setMinimumHeight(150);
        selectImg1.setMinimumWidth(150);
        selectImg1.setMaxHeight(150);
        selectImg1.setMaxWidth(150);
        selectImg1.setImageResource(android.R.drawable.ic_delete);
        selectImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CARREGA_IMG1);
            }
        });
        //layout.addView(selectImg1);

        /////////////////////////////////////////

        selectImg2 = new ImageView(this);
        selectImg2.setMinimumHeight(150);
        selectImg2.setMinimumWidth(150);
        selectImg2.setMaxHeight(150);
        selectImg2.setMaxWidth(150);
        selectImg2.setImageResource(android.R.drawable.ic_delete);
        selectImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CARREGA_IMG2);
            }
        });
        //layout.addView(selectImg2);

        // Botons d'esborrat
        Button clear1 = new Button (this);
        clear1.setText("Esborra IMG1");
        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path1 = null;
                selectImg1.setImageResource(android.R.drawable.ic_delete);
            }
        });

        Button clear2 = new Button (this);
        clear2.setText("Esborra IMG2");
        clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path2 = null;
                selectImg2.setImageResource(android.R.drawable.ic_delete);
            }
        });

        LinearLayout layImgs = new LinearLayout(this);
        layImgs.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout layElem = new LinearLayout(this);
        layElem.setOrientation(LinearLayout.VERTICAL);
        layElem.addView(selectImg1);
        layElem.addView(selectImg2);
        layElem.setMinimumWidth(500);
        layImgs.addView(layElem);

        layElem = new LinearLayout(this);
        layElem.setOrientation(LinearLayout.VERTICAL);
        layElem.addView(clear1);
        layElem.addView(clear2);
        layImgs.addView(layElem);
        layout.addView(layImgs);


        // Creem AlertDialog que demanarà dades
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Afegir Moneda")
                .setPositiveButton("Cancel·la", null) // Canviem ordre del botons per tenir Cancel·lar a la dreta (ICS)
                .setNegativeButton("Afegeix", null)
                .create();

        // Sobreescrivim el botó perque no es tanqui al acceptar si falten camps
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!inputDivisa.getText().toString().isEmpty() && !inputValor.getText().toString().isEmpty()
                                && !inputPais.getText().toString().isEmpty() && !inputAny.getText().toString().isEmpty()
                                && !inputDescripcio.getText().toString().isEmpty()) {
                            // Afegim Moneda
                            coinData.createCoin(inputDivisa.getText().toString(),
                                    Double.parseDouble(inputValor.getText().toString()), Integer.parseInt(inputAny.getText().toString()),
                                    inputPais.getText().toString(), inputDescripcio.getText().toString(), path1, path2);
                            // Refresquem el ArrayAdapter
                            adapter.refresh();
                            // Avisem que tot és correcte
                            Toast.makeText(MainActivity.this, "Afegit Correctament", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(MainActivity.this, "Has d'omplir tots els camps!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button buttonNegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        // Mostrem el dialeg
        dialog.show();
    }


    public void onClickEliminarPrimer(View view) {
        if (adapter.getCount() > 0) {
            Coin coin = adapter.getItem(0);
            coinData.deleteCoin(coin);
            adapter.refresh();
        }
    }

    // Life cycle methods. Check whether it is necessary to reimplement them

    @Override
    protected void onResume() {
        coinData.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        coinData.close();
        super.onPause();
    }

}