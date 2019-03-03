package app.transit.cetle.transitapp;

import android.content.Context;

public interface Contract {
    interface View {
        Context getContext();

        void onResponseString(TransitDataModel[] list, int tag);

        void onErrorResponse(int tag);
    }

    interface Presenter {
        void get(String url, int tag);
    }
}
