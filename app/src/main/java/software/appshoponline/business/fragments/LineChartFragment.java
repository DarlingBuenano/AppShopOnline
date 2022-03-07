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

    private AnyChartView anyChartLine;
    private View root;
    private String datos;
    private JSONArray datosArrayJson;

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
            datosArrayJson = new JSONArray(datos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lineChart(datosArrayJson);
        return root;
    }

    private void lineChart(JSONArray json){
        this.anyChartLine = root.findViewById(R.id.ChartViewLine);
        this.anyChartLine.setProgressBar(root.findViewById(R.id.ChartViewLine_Progressbar));

        Cartesian planoCartesiano = AnyChart.line();
        planoCartesiano.animation(true);
        //planoCartesiano.padding(10d, 20d, 5d, 20d);
        planoCartesiano.crosshair().enabled(true);
        planoCartesiano.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
        planoCartesiano.tooltip().positionMode(TooltipPositionMode.POINT);
        planoCartesiano.title("Resumen de venta de productos");
        planoCartesiano.yAxis(0).title("Cantidad");
        planoCartesiano.xAxis(0).title("Periodo");
        planoCartesiano.xAxis(0).labels().padding(5d,5d,5d,5d);

        List<DataEntry> datosSerie = new ArrayList<>();

        try {
            for (int i = 0; i < json.length(); i++){
                JSONObject producto = json.getJSONObject(i);
                datosSerie.add(new ValueDataEntry(producto.getString("x"), producto.getInt("y")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        Line series = planoCartesiano.line(datosSerie);
        series.hovered().markers().enabled(true);
        series.hovered().markers().type(MarkerType.CIRCLE).size(4d);
        series.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);

        planoCartesiano.legend().enabled(true);
        planoCartesiano.legend().fontSize(13d);
        //planoCartesiano.legend().padding(0d, 0d, 10d, 0d);

        this.anyChartLine.setChart(planoCartesiano);
    }
}