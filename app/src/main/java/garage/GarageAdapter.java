package garage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uc3m.electricapp.R;

import java.util.ArrayList;
import java.util.List;

public class GarageAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<VehiculoGarage> garagelist = null;
    private ArrayList<VehiculoGarage> arraylist;

    private String id;
    private String marca;
    private String modelo;
    private int autonomia;


    public GarageAdapter(Context context,
                         List<VehiculoGarage> garagelist) {
        this.context = context;
        this.garagelist = garagelist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<VehiculoGarage>();
        this.arraylist.addAll(garagelist);
        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder {
        TextView marca;
        TextView modelo;
        ImageView image;
    }

    @Override
    public int getCount() {
        return garagelist.size();
    }

    @Override
    public Object getItem(int position) {
        return garagelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.garage_card, null);
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
        holder.marca.setText(garagelist.get(position).getMarca());
        holder.modelo.setText(garagelist.get(position).getModelo());
        // Set the results into ImageView
        imageLoader.DisplayImage(garagelist.get(position).getImage(),
                holder.image);
        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*id = garagelist.get(position).getIdVehiculo();
                marca = garagelist.get(position).getMarca();
                modelo = garagelist.get(position).getModelo();
                autonomia = garagelist.get(position).getAutonomia();

                //Cerramos la activity y retornamos el poder a la activity para que actúe
                Intent i = ((Activity)context).getIntent();

                //Devolvemos los datos que ha introducido el usuario delegando la tarea
                i.putExtra("marca", marca);
                i.putExtra("modelo", modelo);
                i.putExtra("autonomia", autonomia);

                //Cerramos la pantalla indicando que ha ido bien
                ((Activity)context).setResult(((Activity)context).RESULT_OK, i);

                ((Activity)context).finish();*/

            }
        });
        return view;
    }

}
