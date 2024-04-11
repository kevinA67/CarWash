package com.example.grupo6.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo6.Config.Servicios;
import com.example.grupo6.R;

import java.util.List;

public class ListAdapterServicios extends RecyclerView.Adapter<ListAdapterServicios.ViewHolder> {

    private List<Servicios> datos;
    private LayoutInflater inflater;
    private Context context;
    //public static int selectedItem = -1;

    public ListAdapterServicios(List<Servicios> itemList, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.datos = itemList;
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    //public static int getSelectedItem() {
   //     return selectedItem;
    //}

    @Override
    public ListAdapterServicios.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View view = inflater.inflate(R.layout.disenio_servicios, null);
        return new ListAdapterServicios.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapterServicios.ViewHolder holder, final int position) {
        holder.bindData(datos.get(position));

        //PARA QUE EL SELECTOR FUNCIONE
        //final int currentPosition = position;
        //holder.itemView.setSelected(position == selectedItem);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Actualizar el índice del elemento seleccionado
//          //      selectedItem = currentPosition;
//
//                // Notificar al adaptador de los cambios
//            //    notifyDataSetChanged();
//            }
//        });
    }

    public void setItems(List<Servicios> items) {
        datos = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, cantidad, precio;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nombreServicios);
            //cantidad=(TextView) itemView.findViewById(R.id.cantidadServicios);
            precio=(TextView) itemView.findViewById(R.id.precioServicios);

        }

        void bindData(final Servicios servicios) {
            nombre.setText(servicios.getNombre());
            //cantidad.setText(servicios.getCantidad());
            precio.setText(servicios.getPrecio());
        }
    }

    public void setFilteredList(List<Servicios> filteredList) {
        this.datos = filteredList;
        notifyDataSetChanged();
    }
}

