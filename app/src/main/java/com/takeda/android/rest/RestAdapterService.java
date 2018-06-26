package com.takeda.android.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.skk.lib.utils.SessionManager;
import com.takeda.android.TakedaApplication;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by microlentsystems on 21/03/18.
 */

public class RestAdapterService {

  RestAdapterService() {

  }

  public static <S> S createService(Class<S> serviceClass) {

    final SessionManager session = new SessionManager(TakedaApplication.getContext());

    Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .create();

    RestAdapter adapter = new RestAdapter.Builder()
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setEndpoint(session.getBaseURL())
        .setConverter(new GsonconverterWrapper(gson)).build();

    return adapter.create(serviceClass);
  }


}

class GsonconverterWrapper implements Converter {

  private GsonConverter converter;

  public GsonconverterWrapper(Gson gson) {
    converter = new GsonConverter(gson);

  }

  @Override
  public Object fromBody(TypedInput body, Type type) throws ConversionException {

    byte[] bodyBytes = readInBytes(body);

    TypedByteArray newBody = new TypedByteArray(body.mimeType(), bodyBytes);

    try {
      return converter.fromBody(newBody, type);
    } catch (ConversionException e) {
      if (e.getCause() instanceof JsonParseException && type.equals(String.class)) {
        return new String(bodyBytes);
      } else {
        throw e;
      }
    }
  }

  @Override
  public TypedOutput toBody(Object object) {
    return converter.toBody(object);
  }

  private byte[] readInBytes(TypedInput body) throws ConversionException {
    InputStream in = null;
    try {
      in = body.in();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      byte[] buffer = new byte[0xFFFF];

      for (int len; (len = in.read(buffer)) != -1; ) {
        os.write(buffer, 0, len);
      }
      os.flush();

      return os.toByteArray();
    } catch (IOException e) {
      throw new ConversionException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ignored) {

        }
      }
    }

  }

}

