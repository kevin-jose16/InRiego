package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.olave.inriego.AdapterPivots;
import com.example.olave.inriego.DatePickerFragment_Lluvia;
import com.example.olave.inriego.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fm_agregarLluvia.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fm_agregarLluvia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fm_agregarLluvia extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static final int DATE_DIALOG_ID = 0;
    TextView tvdate;
    ListView lv;
    ArrayList<String> pivots = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public Fm_agregarLluvia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fm_agregarLluvia.
     */
    // TODO: Rename and change types and number of parameters
    public static Fm_agregarLluvia newInstance(String param1, String param2) {
        Fm_agregarLluvia fragment = new Fm_agregarLluvia();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_agregar_lluvia, container, false);
        lv = (ListView) rootview.findViewById(R.id.lst_lluvia);
        pivots.add("P1");
        pivots.add("P2");
        pivots.add("P3");
        final AdapterPivots adapter = new AdapterPivots(getActivity(),pivots);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {


                Object obj = adapter.getItem(view.getId());
                view.setSelected(true);

            }

        });

        // Inflate the layout for this fragment
        return  rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void showDatePickerDialog_Lluvia(View v) {
        DatePickerFragment_Lluvia newFrag = new DatePickerFragment_Lluvia();
        this.getActivity().getBaseContext();
        newFrag.show(getActivity().getSupportFragmentManager(), "datePicker");
        newFrag.SetearFechas("2017-09-24");
    }
}
