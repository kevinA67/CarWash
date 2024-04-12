package com.example.grupo6.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo6.Config.Vehiculo;
import com.example.grupo6.R;

import java.util.List;

public class ListAdapterVehiculo extends RecyclerView.Adapter<ListAdapterVehiculo.ViewHolder>{

        private Context mContext;
        private List<Vehiculo> mVehiculos;
        private LayoutInflater inflater;
        private OnItemClickListener mListener;

        //public static int selectedItem = -1;


    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }
        public ListAdapterVehiculo(Context context, List<Vehiculo> vehiculos) {
            this.inflater = LayoutInflater.from(context);
            this.mContext = context;
            this.mVehiculos = vehiculos;
        }



        public void setOnItemClickListener(OnItemClickListener listener){
            mListener=listener;
        }

        @Override
        public int getItemCount() {
            return mVehiculos.size();
        }

        //public static int getSelectedItem() {
        //     return selectedItem;
        //}


    //public static int getSelectedItem() {
      //  return selectedItem;
    //}
    @Override
    public ListAdapterVehiculo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View view = inflater.inflate(R.layout.disenio_vehiculo_vista, parent, false);
        return new ListAdapterVehiculo.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ListAdapterVehiculo.ViewHolder holder, final int position) {
        holder.bindData(mVehiculos.get(position));
    }

    public void setItems(List<Vehiculo> items) {
        mVehiculos = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView marca, modelo, anio, color, combustible, placa;
        ImageView eliminar;

        ViewHolder(View itemView) {
            super(itemView);
            marca = (TextView) itemView.findViewById(R.id.txt_marcav);
            //modelo=(TextView) itemView.findViewById(R.id.txt_modelov);
            anio=(TextView) itemView.findViewById(R.id.txt_aniov);
            color=(TextView) itemView.findViewById(R.id.txt_Colorv);
            combustible=(TextView) itemView.findViewById(R.id.txt_combustiblev);
            placa =(TextView) itemView.findViewById(R.id.txt_placav);
            eliminar=(ImageView) itemView.findViewById(R.id.eliminarVehiculo);

            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    mListener.onDeleteClick(position);
                }
                }
            });
        }

        void bindData(final Vehiculo vehiculo) {
            marca.setText(vehiculo.getMarca() + " " + vehiculo.getModelo());
            //modelo.setText(vehiculo.getModelo());
            anio.setText(vehiculo.getAnio());
            color.setText(vehiculo.getColor());
            combustible.setText(vehiculo.getCombustible());
            placa.setText(vehiculo.getPlaca());
        }
    }

    public void setFilteredList(List<Vehiculo> filteredList) {
        this.mVehiculos = filteredList;
        notifyDataSetChanged();
    }
}
