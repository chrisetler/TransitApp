package app.transit.cetle.transitapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.transit.cetle.transitapp.databinding.DeparturesRowBinding;

public class DeparturesRecyclerViewAdapter extends RecyclerView.Adapter<DeparturesRecyclerViewAdapter.ViewHolder> {

    Context context;
    private TransitDataModel[] mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String endpoint; // the service endpoint used for updated this RecyclerView

    // data is passed into the constructor
    DeparturesRecyclerViewAdapter(Context context, String endpoint) {
        this.context = context;
        this.endpoint = endpoint;
        this.mInflater = LayoutInflater.from(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        DeparturesRowBinding binding = DeparturesRowBinding.inflate(mInflater,R.layout.departures_row, parent, false);
        DeparturesRowBinding binding = DataBindingUtil.inflate(mInflater, R.layout.departures_row, parent, false);
        return new ViewHolder(binding);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransitDataModel model = mData[position];
        holder.binding.name.setText(model.name);
        holder.binding.stop.setText(model.stop);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(model.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date now = new Date();

        if (parse != null) {
            SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
            holder.binding.timeClock.setText(timeformat.format(parse));
        }


        Boolean delayed = model.delayed;
        if (delayed) {
            holder.binding.timeUntil.setText("Delayed");
            holder.binding.timeUntil.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (model.minutesUntil != null) {
            holder.binding.timeUntil.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.binding.timeUntil.setText(model.minutesUntil + "min");
        } else {
            holder.binding.timeUntil.setTextColor(ContextCompat.getColor(context, R.color.green));
            if (parse != null) {
                long diff = parse.getTime() - now.getTime();
                long minutes = diff / 60000;
                holder.binding.timeUntil.setText(minutes + "min");
            }
        }

        holder.binding.getRoot().setOnClickListener(view -> {
            if (model.minutesUntil == null) {
                Toast.makeText(context, "Cannot track light rail", Toast.LENGTH_LONG).show();
            } else {
                ServiceSingleton.getInstance().start(model, endpoint, context);
            }

        });


//        holder.myTextView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.length;
    }

    public void setData(TransitDataModel[] data) {
        this.mData = data;
        notifyDataSetChanged();
    }

//    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData[id];
//    }

//    // allows clicks events to be caught
//    void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        DeparturesRowBinding binding;

        ViewHolder(DeparturesRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

}