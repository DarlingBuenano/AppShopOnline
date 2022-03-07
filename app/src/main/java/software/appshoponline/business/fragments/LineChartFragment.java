package software.appshoponline.business.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software.appshoponline.R;

public class LineChartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View root;
    private String datos;

    public LineChartFragment() {
        // Required empty public constructor
    }

    public static LineChartFragment newInstance(String param1, String param2) {
        LineChartFragment fragment = new LineChartFragment();
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
        root = inflater.inflate(R.layout.fragment_line_chart, container, false);
        try {
            JSONArray datosArrayJson = new JSONArray(datos);
            lineChart(datosArrayJson);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
        return root;
    }

    private void lineChart(JSONArray json){
        AnyChartView anyChartLine;
        anyChartLine = root.findViewById(R.id.ChartViewLine);
        anyChartLine.setProgressBar(root.findViewById(R.id.ChartViewLine_Progressbar));

        Cartesian planoCartesiano = AnyChart.line();
        planoCartesiano.removeAllSeries();

        planoCartesiano.animation(true);
        planoCartesiano.crosshair().enabled(true);
        planoCartesiano.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
        planoCartesiano.tooltip().positionMode(TooltipPositionMode.POINT);
        planoCartesiano.title("Resumen de ingresos por ventas de productos");
        planoCartesiano.yAxis(0).title("Cantidad en dólares vendidos");
        planoCartesiano.xAxis(0).title("Periodo");
        planoCartesiano.xAxis(0).labels().padding(5d,5d,5d,5d);

        List<DataEntry> datosSerie = new ArrayList<>();
        datosSerie.clear();
        try {
            for (int i = 0; i < json.length(); i++){
                JSONObject venta = json.getJSONObject(i);
                datosSerie.add(new ValueDataEntry(venta.getString("x"), venta.getDouble("y")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }

        Line series = planoCartesiano.line(datosSerie);
        series.hovered().markers().enabled(true);
        series.hovered().markers().type(MarkerType.CIRCLE).size(4d);
        series.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);

        planoCartesiano.legend().enabled(true);
        planoCartesiano.legend().fontSize(13d);
        planoCartesiano.legend().title("Ventas");
        //planoCartesiano.legend().padding(0d, 0d, 10d, 0d);

        anyChartLine.setChart(planoCartesiano);
        anyChartLine.invalidate();
    }
}