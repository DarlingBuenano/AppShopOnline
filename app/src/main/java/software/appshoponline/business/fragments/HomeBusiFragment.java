package software.appshoponline.business.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import software.appshoponline.R;

public class HomeBusiFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View root;
    Button btnVegetalesBusi;
    Button btnFrutasBusi;
    Button btnGranosBusi;
    Button btnOtrosBusi;
    FragmentTransaction transaction;
    Fragment frg_vegetalesBusi;
    Fragment frg_frutasBusi;
    Fragment frg_granosBusi;
    Fragment frg_otrosBusi;

    public HomeBusiFragment() {
        // Required empty public constructor
    }

    public static HomeBusiFragment newInstance(String param1, String param2) {
        HomeBusiFragment fragment = new HomeBusiFragment();
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
        frg_vegetalesBusi = new VegetalBusiFragment();
        frg_frutasBusi = new FruitBusiFragment();
        frg_granosBusi = new GrainBusiFragment();
        frg_otrosBusi = new OtherBusiFragment();

        root = inflater.inflate(R.layout.fragment_home_busi, container, false);

        btnVegetalesBusi = root.findViewById(R.id.frghomebusi_btnVegetales);
        btnFrutasBusi = root.findViewById(R.id.frghomebusi_btnFrutas);
        btnGranosBusi = root.findViewById(R.id.frghomebusi_btnGranos);
        btnOtrosBusi = root.findViewById(R.id.frghomebusi_btnOtros);

        transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frghomebusi_container_fragments, frg_vegetalesBusi);
        transaction.commit();

        btnVegetalesBusi.setOnClickListener(btnMostrarFragmentProductos);
        btnFrutasBusi.setOnClickListener(btnMostrarFragmentProductos);
        btnGranosBusi.setOnClickListener(btnMostrarFragmentProductos);
        btnOtrosBusi.setOnClickListener(btnMostrarFragmentProductos);

        return root;
    }

    private View.OnClickListener btnMostrarFragmentProductos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            transaction = getParentFragmentManager().beginTransaction();
            limpiarEstilos();
            switch (view.getId()){
                case R.id.frghomebusi_btnVegetales:
                    transaction.replace(R.id.frghomebusi_container_fragments, frg_vegetalesBusi);
                    btnVegetalesBusi.setTextColor(getResources().getColor(R.color.app_background));
                    btnVegetalesBusi.setBackgroundResource(R.drawable.shp_btn_header_lef_selected);
                    break;
                case R.id.frghomebusi_btnFrutas:
                    transaction.replace(R.id.frghomebusi_container_fragments, frg_frutasBusi);
                    btnFrutasBusi.setTextColor(getResources().getColor(R.color.app_background));
                    btnFrutasBusi.setBackgroundResource(R.drawable.shp_btn_header_selected);
                    break;
                case R.id.frghomebusi_btnGranos:
                    transaction.replace(R.id.frghomebusi_container_fragments, frg_granosBusi);
                    btnGranosBusi.setTextColor(getResources().getColor(R.color.app_background));
                    btnGranosBusi.setBackgroundResource(R.drawable.shp_btn_header_selected);
                    break;
                case R.id.frghomebusi_btnOtros:
                    transaction.replace(R.id.frghomebusi_container_fragments, frg_otrosBusi);
                    btnOtrosBusi.setTextColor(getResources().getColor(R.color.app_background));
                    btnOtrosBusi.setBackgroundResource(R.drawable.shp_btn_header_right_selected);
                    break;
            }
            transaction.commit();
        }
    };

    private void limpiarEstilos(){
        btnVegetalesBusi.setBackgroundResource(R.drawable.shp_btn_header_lef);
        btnVegetalesBusi.setTextColor(getResources().getColor(R.color.app_font));

        btnFrutasBusi.setBackgroundResource(R.drawable.shp_btn_header);
        btnFrutasBusi.setTextColor(getResources().getColor(R.color.app_font));

        btnGranosBusi.setBackgroundResource(R.drawable.shp_btn_header);
        btnGranosBusi.setTextColor(getResources().getColor(R.color.app_font));

        btnOtrosBusi.setBackgroundResource(R.drawable.shp_btn_header_right);
        btnOtrosBusi.setTextColor(getResources().getColor(R.color.app_font));
    }
}