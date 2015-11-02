package com.hak.wymi.persistance.pojos.trial;

import com.hak.wymi.persistance.interfaces.SecureToSend;
import org.joda.time.DateTime;

import java.util.List;

public interface Trial extends SecureToSend {
    DateTime getCreated();

    List<? extends Juror> getJurors();
}
