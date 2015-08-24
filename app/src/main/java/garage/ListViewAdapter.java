package garage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.uc3m.electricapp.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<Vehiculo> vehiclelist = null;
    private ArrayList<Vehiculo> arraylist;

    private String id;
    private String marca;
    private String modelo;
    private int autonomia;

    public int result = ((Activity) context).RESULT_CANCELED;


    ParseUser currentUser = ParseUser.getCurrentUser();



    public ListViewAdapter(Context context,
                           List<Vehiculo> vehiclelist) {
        this.context = context;
        this.vehiclelist = vehiclelist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Vehiculo>();
        this.arraylist.addAll(vehiclelist);
        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder {
        TextView marca;
        TextView modelo;
        ImageView image;
    }

    @Override
    public int getCount() {
        return vehiclelist.size();
    }

    @Override
    public Object getItem(int position) {
        return vehiclelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.vehicle_card, null);
            // Locate the TextViews in listview_item.xml
            holder.marca = (TextView) view.findViewById(R.id.marca);
            holder.modelo = (TextView) view.findViewById(R.id.modelo);
            // Locate the ImageView in listview_item.xml
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.marca.setText(vehiclelist.get(position).getMarca());
        holder.modelo.setText(vehiclelist.get(position).getModelo());
        // Set the results into ImageView
        imageLoader.DisplayImage(vehiclelist.get(position).getImage(),
                holder.image);
        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ParseQuery<ParseObject> queryGarage = new ParseQuery<ParseObject>(
                        "Garage");


                id = vehiclelist.get(position).getIdVehiculo();

                try{
                    queryGarage.whereEqualTo("user", currentUser.getUsername());
                    List<ParseObject> scoreList = queryGarage.find();

                    boolean coincide = false;

                    for (int i = 0; i < scoreList.size(); i++) {
                        ParseObject row = scoreList.get(i);
                        String idVehicleGarage = (String) row.get("vehicle");

                        if(idVehicleGarage.equals(id)){
                            coincide = true;
                        }
                    }

                    if(coincide == false) {
                        ParseObject garage = new ParseObject("Garage");
                        garage.put("user", currentUser.getUsername());
                        garage.put("vehicle", id);
                        garage.save();
                        result = ((Activity) context).RESULT_OK;
                    }else{
                        Toast.makeText(context, "Ya dispone de este vehiculo en su garage", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Cerramos la activity y retornamos el poder a la activity para que actúe
                Intent i = ((Activity)context).getIntent();

                /*id = vehiclelist.get(position).getIdVehiculo();
                marca = vehiclelist.get(position).getMarca();
                modelo = vehiclelist.get(position).getModelo();
                autonomia = vehiclelist.get(position).getAutonomia();

                //Cerramos la activity y retornamos el poder a la activity para que actúe
                Intent i = ((Activity)context).getIntent();

                //Devolvemos los datos que ha introducido el usuario delegando la tarea
                i.putExtra("marca", marca);
                i.putExtra("modelo", modelo);
                i.putExtra("autonomia", autonomia);*/

                //Cerramos la pantalla indicando que ha ido bien
                ((Activity)context).setResult(result, i);

                ((Activity)context).finish();

            }
        });
        return view;
    }

}
