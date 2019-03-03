package app.transit.cetle.transitapp;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class Presenter implements Contract.Presenter {


    Contract.View view;

    RequestQueue queue;

    public Presenter(Contract.View view) {
        this.view = view;
        queue = Volley.newRequestQueue(view.getContext());

    }


    @Override
    public void get(String url, int tag) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {

            Gson gson = new Gson();
            TransitDataModel[] list = gson.fromJson(response, TransitDataModel[].class);

            view.onResponseString(list, tag);
        }, error -> {
            view.onErrorResponse(tag);
        });

        queue.add(stringRequest);
    }
}
