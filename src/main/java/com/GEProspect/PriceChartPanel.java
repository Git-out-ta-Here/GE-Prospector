package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceChartPanel extends JPanel {
    private final TimeSeries buyPriceSeries;
    private final TimeSeries sellPriceSeries;
    private final ChartPanel chartPanel;
    
    public PriceChartPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        // Create time series for buy and sell prices
        buyPriceSeries = new TimeSeries("Buy Price");
        sellPriceSeries = new TimeSeries("Sell Price");
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(buyPriceSeries);
        dataset.addSeries(sellPriceSeries);
        
        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            null,           // No title (we'll use panel title)
            "Time",        // x-axis label
            "Price",       // y-axis label
            dataset,       // data
            true,         // include legend
            true,         // tooltips
            false         // urls
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(ColorScheme.DARKER_GRAY_COLOR);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(ColorScheme.DARKER_GRAY_COLOR);
        plot.setDomainGridlinePaint(ColorScheme.MEDIUM_GRAY_COLOR);
        plot.setRangeGridlinePaint(ColorScheme.MEDIUM_GRAY_COLOR);
        
        // Customize date axis
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        axis.setLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        axis.setTickLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        
        // Customize price axis
        plot.getRangeAxis().setLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        plot.getRangeAxis().setTickLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        
        // Customize series colors
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, ColorScheme.PROGRESS_ERROR_COLOR); // Buy price in red
        renderer.setSeriesPaint(1, ColorScheme.PROGRESS_COMPLETE_COLOR); // Sell price in green
        plot.setRenderer(renderer);
        
        // Create chart panel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        add(chartPanel, BorderLayout.CENTER);
    }
    
    public void addPrice(int buyPrice, int sellPrice) {
        Minute minute = new Minute(new Date());
        buyPriceSeries.addOrUpdate(minute, buyPrice);
        sellPriceSeries.addOrUpdate(minute, sellPrice);
        
        // Remove old data points (keep last 24 hours)
        while (buyPriceSeries.getItemCount() > 1440) { // 24 hours * 60 minutes
            buyPriceSeries.delete(0);
            sellPriceSeries.delete(0);
        }
    }
    
    public void clearData() {
        buyPriceSeries.clear();
        sellPriceSeries.clear();
    }
}