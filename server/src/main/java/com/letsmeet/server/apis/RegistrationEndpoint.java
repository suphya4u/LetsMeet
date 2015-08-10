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
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.letsmeet.server.apis.messages.RegistrationResponse;
import com.letsmeet.server.data.UserRecord;
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
@Api(name = "registration", version = "v1",
    namespace = @ApiNamespace(ownerDomain = "server.letsmeet.com",
        ownerName = "server.letsmeet.com",
        packagePath = ""))
public class RegistrationEndpoint {

  private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

  /**
   * Register a device to the backend
   *
   * @param request The Google Cloud Messaging registration record to add
   */
  @ApiMethod(name = "register")
  public RegistrationResponse registerDevice(RegistrationRequest request) {
    // Match records by phone number.
    UserRecord record = findExistingRecord(request);
    if (record != null) {
      log.info("Device " + request.getRegId() + " already registered, updating record");
    } else {
      record = new UserRecord();
    }
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
    UserRecord record = ofy().load().type(UserRecord.class)
        .filter("regId", regId).first().now();
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
  public CollectionResponse<UserRecord> listDevices(@Named("count") int count) {
    List<UserRecord> records = ofy().load().type(UserRecord.class).limit(count).list();
    return CollectionResponse.<UserRecord>builder().setItems(records).build();
  }

  private UserRecord findExistingRecord(RegistrationRequest request) {
    List<UserRecord> recordByPhoneList = ofy().load().type(UserRecord.class)
        .filter("phoneNumber", request.getPhoneNumber()).list();
    Preconditions.checkArgument(recordByPhoneList.size() < 2, "Too many records by phone number");
    if (!recordByPhoneList.isEmpty()) {
      UserRecord recordByPhone = recordByPhoneList.get(0);
      if (!Strings.isNullOrEmpty(recordByPhone.getRegId())
          && !recordByPhone.getRegId().equals(request.getRegId())) {
        // TODO(suhas): Phone number is already signed up. We may need extra verification in this case.
        // Allowing it for now.
        log.severe("Record with phone [" + request.getPhoneNumber()
            + "] already exists with regId [" + request.getRegId());
      }
      return recordByPhone;
    }

    return ofy().load().type(UserRecord.class)
        .filter("regId", request.getRegId()).first().now();
  }

}
