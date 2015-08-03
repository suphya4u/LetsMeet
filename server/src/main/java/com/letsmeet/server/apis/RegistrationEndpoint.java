/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.letsmeet.server.apis.messages.RegistrationResponse;
import com.letsmeet.server.data.RegistrationRecord;
import com.letsmeet.server.apis.messages.RegistrationRequest;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.letsmeet.server.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(name = "registration", version = "v1", namespace = @ApiNamespace(ownerDomain = "server.letsmeet.com", ownerName = "server.letsmeet.com", packagePath = ""))
public class RegistrationEndpoint {

  private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

  /**
   * Register a device to the backend
   *
   * @param request The Google Cloud Messaging registration record to add
   */
  @ApiMethod(name = "register")
  public RegistrationResponse registerDevice(RegistrationRequest request) {
    if (findRecord(request.getRegId()) != null) {
      log.info("Device " + request.getRegId() + " already registered, skipping register");
      return new RegistrationResponse().setIsSuccess(true);
    }
    RegistrationRecord record = new RegistrationRecord();
    record.setRegId(request.getRegId());
    record.setName(request.getName());
    record.setPhoneNumber(request.getPhoneNumber());
    long userId = ofy().save().entity(record).now().getId();
    return new RegistrationResponse().setIsSuccess(true).setUserId(userId);
  }

  /**
   * Unregister a device from the backend
   *
   * @param regId The Google Cloud Messaging registration Id to remove
   */
  @ApiMethod(name = "unregister")
  public void unregisterDevice(@Named("regId") String regId) {
    RegistrationRecord record = findRecord(regId);
    if (record == null) {
      log.info("Device " + regId + " not registered, skipping unregister");
      return;
    }
    ofy().delete().entity(record).now();
  }

  /**
   * Return a collection of registered devices
   *
   * @param count The number of devices to list
   * @return a list of Google Cloud Messaging registration Ids
   */
  @ApiMethod(name = "listDevices")
  public CollectionResponse<RegistrationRecord> listDevices(@Named("count") int count) {
    List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(count).list();
    return CollectionResponse.<RegistrationRecord>builder().setItems(records).build();
  }

  private RegistrationRecord findRecord(String regId) {
    return ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
  }

}
