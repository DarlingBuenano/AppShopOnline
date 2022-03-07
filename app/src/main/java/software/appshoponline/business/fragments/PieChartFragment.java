package software.appshoponline.business.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software.appshoponline.R;

public class PieChartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View root;
    private String datos;
    private JSONArray datosArrayJson;

    public PieChartFragment() {
        // Required empty public constructor
    }

    public static PieChartFragment newInstance(String param1, String param2) {
        PieChartFragment fragment = new PieChartFragment();
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

            datos = getArguments().getString("datos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        try {
            datosArrayJson = new JSONArray(datos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pieChart(datosArrayJson);
        return root;
    }

    private void pieChart(JSONArray json){
        AnyChartView anyChartPie;
        anyChartPie = root.findViewById(R.id.ChartViewPie);
        anyChartPie.setProgressBar(root.findViewById(R.id.ChartViewPie_Progressbar));

        Pie pie = AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(getContext(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> datos = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++){
                JSONObject producto = json.getJSONObject(i);
                datos.add(new ValueDataEntry(producto.getString("x"), producto.getDouble("y")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        pie.data(datos);
        pie.title("Resumen de ingresos por ventas de productos");
        pie.labels().position("outside");
        //pie.legend().title().enabled(true);
        //pie.legend().title().text("retail channels").padding(0d, 0d, 10d, 0d);
        //pie.legend().position("center-bootom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);

        anyChartPie.setChart(pie);
        anyChartPie.invalidate();
    }
}