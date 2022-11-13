package com.opsc.monumental.Model.WeatherModel;

public class Forecast
{
    public String cod ;
    public double message ;
    public City city ;


    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public double getMessage() {
        return message;
    }

    public void setMessage(double message) {
        this.message = message;
    }


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
