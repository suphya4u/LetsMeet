package com.letsmeet.android.common;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

/**
 * Helper class to help with phone number utilities.
 */
public class PhoneNumberHelper {

  private final Context context;
  private final PhoneNumberUtil phoneNumberUtil;

  public PhoneNumberHelper(Context context) {
    this.context = context;
    this.phoneNumberUtil = PhoneNumberUtil.getInstance();
  }

  public String formatPhoneNumber(String phoneNumber) {
    // Replace leading zero.
    // TODO(suhas): Do we want to handle numbers starting with 00?
    phoneNumber = phoneNumber.replaceFirst("^0", "");
    String countryCode = getCountryCode();
    String formattedNumber;
    try {
      Phonenumber.PhoneNumber phoneNumberProto = phoneNumberUtil.parse(phoneNumber, countryCode);
      formattedNumber = phoneNumberUtil.format(phoneNumberProto,
          PhoneNumberUtil.PhoneNumberFormat.E164);
    } catch (NumberParseException e) {
      return phoneNumber;
    }
    formattedNumber = PhoneNumberUtils.stringFromStringAndTOA(formattedNumber,
        PhoneNumberUtils.TOA_International);
    return formattedNumber.replaceAll("[^+0-9]", "");
  }

  private String getCountryCode() {
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
      String countryCode = telephonyManager.getNetworkCountryIso();
      if (!Strings.isNullOrEmpty(countryCode)) {
        return countryCode.toUpperCase();
      }
    }

    String countryCode = telephonyManager.getSimCountryIso();
    if (!Strings.isNullOrEmpty(countryCode)) {
      return countryCode.toUpperCase();
    }

    return Locale.getDefault().getCountry().toUpperCase();
  }
}
