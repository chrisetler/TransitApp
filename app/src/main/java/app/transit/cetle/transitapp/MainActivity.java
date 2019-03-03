package app.transit.cetle.transitapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import app.transit.cetle.transitapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements Contract.View {
    public static final int WASH = 0;
    public static final int HOME = 1;
    public static final String url = "http://108.35.15.138:5000/";
    ActivityMainBinding binding;
    Contract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        TabLayout tabLayout = binding.tablayout;
        tabLayout.addTab(tabLayout.newTab().setText("Wash"));
        tabLayout.addTab(tabLayout.newTab().setText("Home"));

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        presenter = new Presenter(this);


    }

    public void getData(String endpoint, int tag) {
        presenter.get(url + endpoint, tag);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onResponseString(TransitDataModel[] list, int tag) {
        DeparturesFragment fragment = (DeparturesFragment) getSupportFragmentManager().getFragments().get(tag);
        fragment.notifyNewList(list);
    }

    @Override
    public void onErrorResponse(int tag) {
        DeparturesFragment fragment = (DeparturesFragment) getSupportFragmentManager().getFragments().get(tag);
        fragment.notifyError();
    }
}
