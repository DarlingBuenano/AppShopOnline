package software.appshoponline.client;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import software.appshoponline.R;
import software.appshoponline.client.fragments.FruitFragment;
import software.appshoponline.client.fragments.GrainFragment;
import software.appshoponline.client.fragments.OtherFragment;
import software.appshoponline.client.fragments.VegetalFragment;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View root;
    Button btnVegetales;
    Button btnFrutas;
    Button btnGranos;
    Button btnOtros;
    FragmentTransaction transaction;
    Fragment frg_vegetales;
    Fragment frg_frutas;
    Fragment frg_granos;
    Fragment frg_otros;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        frg_vegetales = new VegetalFragment();
        frg_frutas = new FruitFragment();
        frg_granos = new GrainFragment();
        frg_otros = new OtherFragment();

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);

        btnVegetales = root.findViewById(R.id.frghome_btnVegetales);
        btnFrutas = root.findViewById(R.id.frghome_btnFrutas);
        btnGranos = root.findViewById(R.id.frghome_btnGranos);
        btnOtros = root.findViewById(R.id.frghome_btnOtros);

        transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frg_container_reciclerviews, frg_vegetales).commit();

        btnVegetales.setOnClickListener(btnMostrarFragmentProductos);
        btnFrutas.setOnClickListener(btnMostrarFragmentProductos);
        btnGranos.setOnClickListener(btnMostrarFragmentProductos);
        btnOtros.setOnClickListener(btnMostrarFragmentProductos);

        return root;
    }

    private View.OnClickListener btnMostrarFragmentProductos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            transaction = getParentFragmentManager().beginTransaction();
            switch (view.getId()){
                case R.id.frghome_btnVegetales:
                    transaction.replace(R.id.frg_container_reciclerviews, frg_vegetales);
                    break;
                case R.id.frghome_btnFrutas:
                    transaction.replace(R.id.frg_container_reciclerviews, frg_frutas);
                    break;
                case R.id.frghome_btnGranos:
                    transaction.replace(R.id.frg_container_reciclerviews, frg_granos);
                    break;
                case R.id.frghome_btnOtros:
                    transaction.replace(R.id.frg_container_reciclerviews, frg_otros);
                    break;
            }
            transaction.commit();
        }
    };
}