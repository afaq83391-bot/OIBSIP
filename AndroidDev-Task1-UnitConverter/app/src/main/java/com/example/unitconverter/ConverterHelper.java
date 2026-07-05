package com.example.unitconverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all unit conversion logic.
 * Each category has a base unit. All conversions go:
 *   source -> base -> target
 * Temperature is handled specially.
 */
public class ConverterHelper {

    // ── Category Labels ──────────────────────────────────────────
    public static final String LENGTH      = "Length";
    public static final String WEIGHT      = "Weight";
    public static final String TEMPERATURE = "Temperature";
    public static final String VOLUME      = "Volume";
    public static final String AREA        = "Area";
    public static final String SPEED       = "Speed";

    // ── Unit Maps (unit name → factor to base unit) ─────────────
    // Base for Length = Meter
    private static final Map<String, Double> LENGTH_MAP = new HashMap<>();
    static {
        LENGTH_MAP.put("Millimeter (mm)",    0.001);
        LENGTH_MAP.put("Centimeter (cm)",    0.01);
        LENGTH_MAP.put("Meter (m)",          1.0);
        LENGTH_MAP.put("Kilometer (km)",     1000.0);
        LENGTH_MAP.put("Inch (in)",          0.0254);
        LENGTH_MAP.put("Foot (ft)",          0.3048);
        LENGTH_MAP.put("Yard (yd)",          0.9144);
        LENGTH_MAP.put("Mile (mi)",          1609.344);
    }

    // Base for Weight = Kilogram
    private static final Map<String, Double> WEIGHT_MAP = new HashMap<>();
    static {
        WEIGHT_MAP.put("Milligram (mg)",     0.000001);
        WEIGHT_MAP.put("Gram (g)",           0.001);
        WEIGHT_MAP.put("Kilogram (kg)",      1.0);
        WEIGHT_MAP.put("Metric Ton (t)",     1000.0);
        WEIGHT_MAP.put("Ounce (oz)",         0.0283495);
        WEIGHT_MAP.put("Pound (lb)",         0.453592);
        WEIGHT_MAP.put("Stone (st)",         6.35029);
    }

    // Base for Volume = Liter
    private static final Map<String, Double> VOLUME_MAP = new HashMap<>();
    static {
        VOLUME_MAP.put("Milliliter (mL)",    0.001);
        VOLUME_MAP.put("Liter (L)",          1.0);
        VOLUME_MAP.put("Cubic Meter (m³)",   1000.0);
        VOLUME_MAP.put("Gallon (US)",        3.78541);
        VOLUME_MAP.put("Quart (US)",         0.946353);
        VOLUME_MAP.put("Pint (US)",          0.473176);
        VOLUME_MAP.put("Cup (US)",           0.236588);
        VOLUME_MAP.put("Fluid Ounce (US)",   0.0295735);
    }

    // Base for Area = Square Meter
    private static final Map<String, Double> AREA_MAP = new HashMap<>();
    static {
        AREA_MAP.put("mm²",                  0.000001);
        AREA_MAP.put("cm²",                  0.0001);
        AREA_MAP.put("m²",                   1.0);
        AREA_MAP.put("km²",                  1000000.0);
        AREA_MAP.put("in²",                  0.00064516);
        AREA_MAP.put("ft²",                  0.092903);
        AREA_MAP.put("Acre",                 4046.86);
        AREA_MAP.put("Hectare",              10000.0);
    }

    // Base for Speed = m/s
    private static final Map<String, Double> SPEED_MAP = new HashMap<>();
    static {
        SPEED_MAP.put("m/s",                 1.0);
        SPEED_MAP.put("km/h",                0.277778);
        SPEED_MAP.put("mph",                 0.44704);
        SPEED_MAP.put("Knot",                0.514444);
        SPEED_MAP.put("ft/s",                0.3048);
    }

    // Temperature units (no factor map – handled in code)
    private static final String[] TEMPERATURE_UNITS = {
            "Celsius (°C)",
            "Fahrenheit (°F)",
            "Kelvin (K)"
    };

    // ── Public API ──────────────────────────────────────────────

    /**
     * Returns the list of units for a given category.
     */
    public static String[] getUnits(String category) {
        switch (category) {
            case LENGTH:      return LENGTH_MAP.keySet().toArray(new String[0]);
            case WEIGHT:      return WEIGHT_MAP.keySet().toArray(new String[0]);
            case TEMPERATURE: return TEMPERATURE_UNITS;
            case VOLUME:      return VOLUME_MAP.keySet().toArray(new String[0]);
            case AREA:        return AREA_MAP.keySet().toArray(new String[0]);
            case SPEED:       return SPEED_MAP.keySet().toArray(new String[0]);
            default:          return new String[0];
        }
    }

    /**
     * Performs the conversion and returns a formatted result string.
     */
    public static String convert(String category, String fromUnit,
                                 String toUnit, double value) {
        double result;

        if (category.equals(TEMPERATURE)) {
            result = convertTemperature(fromUnit, toUnit, value);
        } else {
            Map<String, Double> map = getMap(category);
            if (map == null) return "Error";

            double fromFactor = map.get(fromUnit);
            double toFactor   = map.get(toUnit);

            // source → base → target
            double baseValue  = value * fromFactor;
            result = baseValue / toFactor;
        }

        // Smart formatting: remove trailing zeros
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            // Show up to 6 decimal places, strip trailing zeros
            String formatted = String.format("%.6f", result);
            formatted = formatted.replaceAll("0+$", "");
            formatted = formatted.replaceAll("\\.$", "");
            return formatted;
        }
    }

    /**
     * Returns a human-readable formula string.
     */
    public static String getFormula(String category, String fromUnit,
                                    String toUnit) {
        if (category.equals(TEMPERATURE)) {
            return buildTempFormula(fromUnit, toUnit);
        }
        return "1 " + shortName(fromUnit) + " = " +
                convert(category, fromUnit, toUnit, 1.0) +
                " " + shortName(toUnit);
    }

    // ── Temperature helpers ─────────────────────────────────────

    private static double convertTemperature(String from, String to, double val) {
        // First convert to Celsius
        double celsius;
        switch (from) {
            case "Celsius (°C)":    celsius = val; break;
            case "Fahrenheit (°F)": celsius = (val - 32) * 5.0 / 9.0; break;
            case "Kelvin (K)":      celsius = val - 273.15; break;
            default: return 0;
        }

        // Then from Celsius to target
        switch (to) {
            case "Celsius (°C)":    return celsius;
            case "Fahrenheit (°F)": return celsius * 9.0 / 5.0 + 32;
            case "Kelvin (K)":      return celsius + 273.15;
            default: return 0;
        }
    }

    private static String buildTempFormula(String from, String to) {
        if (from.equals(to)) return "Same unit";
        switch (from + " → " + to) {
            case "Celsius (°C) → Fahrenheit (°F)":
                return "°F = (°C × 9/5) + 32";
            case "Fahrenheit (°F) → Celsius (°C)":
                return "°C = (°F − 32) × 5/9";
            case "Celsius (°C) → Kelvin (K)":
                return "K = °C + 273.15";
            case "Kelvin (K) → Celsius (°C)":
                return "°C = K − 273.15";
            case "Fahrenheit (°F) → Kelvin (K)":
                return "K = (°F − 32) × 5/9 + 273.15";
            case "Kelvin (K) → Fahrenheit (°F)":
                return "°F = (K − 273.15) × 9/5 + 32";
            default: return "";
        }
    }

    // ── Utility ─────────────────────────────────────────────────

    private static Map<String, Double> getMap(String category) {
        switch (category) {
            case LENGTH:  return LENGTH_MAP;
            case WEIGHT:  return WEIGHT_MAP;
            case VOLUME:  return VOLUME_MAP;
            case AREA:    return AREA_MAP;
            case SPEED:   return SPEED_MAP;
            default:      return null;
        }
    }

    /**
     * Extracts the short symbol from a unit string, e.g.
     * "Kilometer (km)" → "km"
     */
    private static String shortName(String full) {
        int start = full.indexOf('(');
        if (start == -1) return full;
        int end = full.indexOf(')', start);
        if (end == -1) return full;
        return full.substring(start + 1, end);
    }
}