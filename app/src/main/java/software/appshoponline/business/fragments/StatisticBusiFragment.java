package software.appshoponline.business.fragments;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;

public class StatisticBusiFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View root;
    private Spinner spinnerPeriodo;
    private Spinner spinnerGrafica;
    private FragmentTransaction transaction;
    private Bundle datos_a_enviar;

    SharedPreferences pref;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String datosJson;
    private int empresa_id;
    private int grafico_index = 0;

    public StatisticBusiFragment() {
        // Required empty public constructor
    }

    public static StatisticBusiFragment newInstance(String param1, String param2) {
        StatisticBusiFragment fragment = new StatisticBusiFragment();
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
        pref = getContext().getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        empresa_id = pref.getInt("empresa_id", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.requestQueue = Volley.newRequestQueue(getContext());
        root = inflater.inflate(R.layout.fragment_statistic_busi, container, false);

        this.spinnerPeriodo = root.findViewById(R.id.frgstatistic_spinnerPeriodos);
        ArrayAdapter<CharSequence> adapterPeriodo = ArrayAdapter.createFromResource(root.getContext(), R.array.spinner_periodo, android.R.layout.simple_spinner_dropdown_item);
        this.spinnerPeriodo.setAdapter(adapterPeriodo);
        this.spinnerPeriodo.setOnItemSelectedListener(clicItemSpinnerPeriodo);

        this.spinnerGrafica = root.findViewById(R.id.frgstatistic_spinnerGrafica);
        ArrayAdapter<CharSequence> adapterGrafica = ArrayAdapter.createFromResource(root.getContext(), R.array.spinner_grafica, android.R.layout.simple_spinner_dropdown_item);
        this.spinnerGrafica.setAdapter(adapterGrafica);
        this.spinnerGrafica.setOnItemSelectedListener(clicItemSpinnerGrafica);

        return root;
    }

    private AdapterView.OnItemSelectedListener clicItemSpinnerPeriodo = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i){
                case 0: //consultar por mes
                    consultarDatosxPeriodo(Dominio.URL_WebServie + Constantes.URL_Mostrar_Productos_Vendidos_x_Mes + "/"+empresa_id);
                    break;
                case 1: //consultar por año
                    consultarDatosxPeriodo(Dominio.URL_WebServie + Constantes.URL_Mostrar_Productos_Vendidos_x_Anio + "/"+empresa_id);
                    break;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };

    private void consultarDatosxPeriodo(String url){
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, onListener, onErrorListener);
        this.requestQueue.add(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> onListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    datosJson = response.getJSONArray("datos").toString();
                    if (datosJson != null){
                        switch (grafico_index){
                            case 0:
                                abrir_frg_lineChart();
                                break;
                            case 1:
                                abrir_frg_columnChart();
                                break;
                            case 2:
                                abrir_frg_pieChart();
                                break;
                        }
                    }
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Parece que no has vendido");
                    builder.setPositiveButton(R.string.dialog_aceptar, null);
                    builder.create().show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener onErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private AdapterView.OnItemSelectedListener clicItemSpinnerGrafica = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            grafico_index = i;
            if (datosJson != null){
                switch (i){
                    case 0:
                        abrir_frg_lineChart();
                        break;
                    case 1:
                        abrir_frg_columnChart();
                        break;
                    case 2:
                        abrir_frg_pieChart();
                        break;
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };

    private void abrir_frg_lineChart(){
        Fragment frglineChart;
        frglineChart = new LineChartFragment();

        datos_a_enviar = new Bundle();
        datos_a_enviar.putString("datos", datosJson);
        frglineChart.setArguments(datos_a_enviar);

        transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frg_container_busi_graphics, frglineChart);
        transaction.commit();
    }

    private void abrir_frg_columnChart(){
        Fragment frgColumnChart;
        frgColumnChart = new ColumnChartFragment();
        datos_a_enviar = new Bundle();
        datos_a_enviar.putString("datos", datosJson);
        frgColumnChart.setArguments(datos_a_enviar);

        transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frg_container_busi_graphics, frgColumnChart);
        transaction.commit();
    }

    private void abrir_frg_pieChart(){
        Fragment frgPieChart;
        frgPieChart = new PieChartFragment();
        datos_a_enviar = new Bundle();
        datos_a_enviar.putString("datos", datosJson);
        frgPieChart.setArguments(datos_a_enviar);

        transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frg_container_busi_graphics, frgPieChart);
        transaction.commit();
    }
}