package software.appshoponline.business.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software.appshoponline.R;

public class ColumnChartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private AnyChartView anyChartColumn;
    private View root;
    private String datos;
    private JSONArray datosArrayJson;

    public ColumnChartFragment() {
        // Required empty public constructor
    }

    public static ColumnChartFragment newInstance(String param1, String param2) {
        ColumnChartFragment fragment = new ColumnChartFragment();
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
        root = inflater.inflate(R.layout.fragment_column_chart, container, false);
        try {
            datosArrayJson = new JSONArray(datos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        columnChart(datosArrayJson);
        return root;
    }

    private void columnChart(JSONArray json){
        this.anyChartColumn = root.findViewById(R.id.ChartViewColumn);
        this.anyChartColumn.setProgressBar(root.findViewById(R.id.ChartViewColumn_Progressbar));

        Cartesian planoCartesiano = AnyChart.column();
        planoCartesiano.animation(true);
        planoCartesiano.title("Resumen de venta");
        planoCartesiano.yScale().minimum(0d);
        planoCartesiano.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        planoCartesiano.tooltip().positionMode(TooltipPositionMode.POINT);
        planoCartesiano.interactivity().hoverMode(HoverMode.BY_X);
        planoCartesiano.xAxis(0).title("Productos");
        planoCartesiano.yAxis(0).title("Cantidad");

        List<DataEntry> datos = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++){
                JSONObject producto = json.getJSONObject(i);
                datos.add(new ValueDataEntry(producto.getString("x"), producto.getInt("y")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        Column column = planoCartesiano.column(datos);
        column.tooltip().titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");
        this.anyChartColumn.setChart(planoCartesiano);
    }
}