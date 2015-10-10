package com.letsmeet.android.apiclient;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.config.Config;
import com.letsmeet.server.registration.Registration;
import com.letsmeet.server.registration.model.RegistrationRequest;
import com.letsmeet.server.registration.model.RegistrationResponse;

import java.io.IOException;

/**
 * Client wrapper for registration service.
 */
public class RegistrationServiceClient {

  private static RegistrationServiceClient selfInstance;
  private static Registration registrationService;

  private RegistrationServiceClient() {}

  public static RegistrationServiceClient getInstance() {
    if (selfInstance == null) {
      selfInstance = new RegistrationServiceClient();
    }
    return selfInstance;
  }

  public RegistrationResponse registerUser(RegistrationRequest request) throws IOException {
    return getService().register(request).execute();
  }

  private Registration getService() {
    if (registrationService != null) {
      return registrationService;
    }

    // TODO(suhas): Common code for all services. Move out.
    Registration.Builder serviceBuilder = new Registration.Builder(
        AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
        .setRootUrl(Config.getServerUrl());

    if (Config.isEmulator()) {
      serviceBuilder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
        @Override
        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
            throws IOException {
          abstractGoogleClientRequest.setDisableGZipContent(true);
        }
      });
    }
    registrationService = serviceBuilder.build();
    return registrationService;
  }
}
