package app.transit.cetle.transitapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.transit.cetle.transitapp.databinding.FragmentDeparturesBinding;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeparturesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeparturesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeparturesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DeparturesRecyclerViewAdapter adapter;
    // TODO: Rename and change types of parameters
    private String endpoint;
    private int tag;
    private FragmentDeparturesBinding binding;
    private OnFragmentInteractionListener mListener;

    public DeparturesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeparturesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeparturesFragment newInstance(String param1, int param2) {
        DeparturesFragment fragment = new DeparturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            endpoint = getArguments().getString(ARG_PARAM1);
            tag = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentDeparturesBinding.inflate(inflater, container, false);

        binding.swipeContainer.setOnRefreshListener(() -> ((MainActivity) getActivity()).getData(endpoint, tag));


        binding.rv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        return binding.getRoot();


//        return inflater.inflate(R.layout.fragment_departures, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();

        binding.swipeContainer.setRefreshing(true);


        //set up RV
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DeparturesRecyclerViewAdapter(getContext(), endpoint);
        binding.rv.setAdapter(adapter);


        mainActivity.getData(endpoint, tag);


    }

    @Override
    public void onResume() {
        super.onResume();

        binding.swipeContainer.setRefreshing(true);
        ((MainActivity) getActivity()).getData(endpoint, tag);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //popuplate the recyclerviwe
    public void notifyNewList(TransitDataModel[] list) {
        binding.swipeContainer.setRefreshing(false);
        adapter.setData(list);


    }

    public void notifyError() {
        binding.swipeContainer.setRefreshing(false);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
