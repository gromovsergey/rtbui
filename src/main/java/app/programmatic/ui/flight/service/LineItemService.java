package app.programmatic.ui.flight.service;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.flight.dao.model.FlightLineItems;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.validation.ValidateLineItem;

import java.time.LocalDateTime;
import java.util.List;


public interface LineItemService extends FlightChannelService {
    LineItem find(Long id);

    List<LineItem> findEagerByFlightId(Long flightId);

    FlightLineItems findEffectiveEager(Long id);

    List<FlightBaseStat> getFlightStat(Long flightId, LocalDateTime startDate, LocalDateTime endDate);

    FlightBaseStat getStat(Long id);

    List<Long> fetchLineItemIds(Long flightId);

    FlightLineItems findEffectiveEagerByFlightId(Long flightId);

    LineItem create(@ValidateLineItem("create") LineItem lineItem);

    LineItem update(@ValidateLineItem("update") LineItem lineItem);

    List<MajorDisplayStatus> activate(@ValidateLineItem("activate") List<Long> ids);

    List<MajorDisplayStatus> inactivate(@ValidateLineItem("inactivate") List<Long> ids);

    List<MajorDisplayStatus> delete(@ValidateLineItem("delete") List<Long> ids);

    void linkAdvertisingChannels(Long id,
                                 @ValidateLineItem("linkAdvertisingChannels") List<Long> channelIds,
                                 boolean linkSpecialChannelFlag);

    void linkSites(Long id, List<Long> siteIds);

    void linkConversions(Long id, List<Long> conversionIds);

    void linkCreatives(Long id, List<Long> creativeIds);
}
