package app.programmatic.ui.flight.service;

import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;

import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.entity.Status;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.campaign.dao.model.CampaignFlightPart;
import app.programmatic.ui.campaign.service.CampaignService;
import app.programmatic.ui.changetrack.dao.model.TableName;
import app.programmatic.ui.changetrack.service.ChangeTrackerService;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareMethod;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.file.service.FileService;
import app.programmatic.ui.flight.dao.CampaignAllocationRepository;
import app.programmatic.ui.flight.dao.FlightRepository;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.dao.model.stat.FlightDashboardStat;
import app.programmatic.ui.flight.tool.CampaignBuilder;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;
import app.programmatic.ui.flight.tool.LineItemBuilder;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.service.RestrictionService;
import app.programmatic.ui.user.dao.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
@PrePersistAwareService(storedValueGetter = "find")
@Validated
public class FlightServiceImpl extends FlightBaseServiceImpl implements FlightServiceInternal {
    private static final Logger logger = Logger.getLogger(FlightServiceImpl.class.getName());

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CampaignAllocationRepository campaignAllocationRepository;

    @Autowired
    private LineItemServiceInternal lineItemService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private FileService fileService;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private ChangeTrackerService changeTrackerService;

    @Autowired
    private DataSourceService dsService;


    @Override
    public List<FlightBaseStat> getAccountStat(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcOperations.query("select * from statqueries.flight_dashboard(?::date, ?::date, ?::int, ?, ?)",
                new Object[]{
                        startDate == null ? null : Timestamp.valueOf(startDate),
                        endDate == null ? null : Timestamp.valueOf(endDate),
                        accountId,
                        Boolean.TRUE,
                        Boolean.FALSE // ToDo: get from user,
                },
                (ResultSet rs, int index) -> rsToStat(rs)
        );
    }

    @Override
    public List<IdName> getAccountFlightList(Long accountId) {
        return jdbcOperations.query("select * from statqueries.get_flights_by_account(?::int)",
                new Object[]{accountId},
                (ResultSet rs, int index) -> new IdName(rs.getLong("flight_id"), rs.getString("name"))
        );
    }

    @Override
    public FlightBaseStat getStat(Long id) {
        return jdbcOperations.queryForObject("select * from statqueries.flight_total_stats(?::int)",
                new Object[]{id},
                (ResultSet rs, int index) -> {
                    FlightBaseStat stat = rsToStat(rs);
                    stat.setBudget(rs.getBigDecimal("budget"));
                    stat.setSpentBudget(rs.getBigDecimal("spent_budget"));
                    stat.setPostImpConv(rs.getLong("post_imp_conv"));
                    stat.setPostClickConv(rs.getLong("post_click_conv"));

                    return stat;
                });
    }

    @Override
    public List<FlightDashboardStat> getDashboardStats(Integer days_back_count) {
        User user = authorizationService.getAuthUser();
        boolean isInternal = user.getUserRole().getAccountRole() == INTERNAL;
        Long accountIdFilter = isInternal ? null : user.getAccountId();

        return dsService.executeWithAuth(jdbcOperations, () -> getDashboardStatsImpl(accountIdFilter, days_back_count));
    }

    private List<FlightDashboardStat> getDashboardStatsImpl(Long accountIdFilter, Integer days_back_count) {
        return jdbcOperations.query("select * from statqueries.adops_dashboard_flights(?::int, ?::int)",
                new Object[]{accountIdFilter, days_back_count},
                (ResultSet rs, int index) -> {
                    FlightDashboardStat stat = new FlightDashboardStat();

                    stat.setAgencyName(rs.getString("agency_name"));
                    stat.setAgencyId(rs.getLong("agency_id"));
                    stat.setAdvertiserName(rs.getString("advertiser_name"));
                    stat.setAdvertiserId(rs.getLong("advertiser_id"));
                    stat.setFlightName(rs.getString("flight_name"));
                    stat.setFlightId(rs.getLong("flight_id"));

                    CampaignDisplayStatus campaignDisplayStatus = CampaignDisplayStatus.valueOf(rs.getInt("flight_status"));
                    stat.setDisplayStatus(campaignDisplayStatus.getMajorStatus());
                    stat.setAlertReason(alertReasonToString(rs.getInt("reason"), campaignDisplayStatus));

                    return stat;
                });
    }

    private String alertReasonToString(Integer alertReasonId, CampaignDisplayStatus status) {
        String alertReasonKey;
        switch (alertReasonId) {
            case 0:
                alertReasonKey = status.getDescriptionKey();
                break;
            case 1:
                alertReasonKey = "flight.alertReason.statisticAlert";
                break;
            case 2:
                alertReasonKey = "flight.alertReason.newFlightAlert";
                break;
            default:
                throw new IllegalArgumentException("Unexpected alert reason id: " + alertReasonId);
        }

        return MessageInterpolator.getDefaultMessageInterpolator().interpolate(alertReasonKey);
    }

    @Override
    public Flight find(Long id) {
        Flight flight = id == null ? null : flightRepository.findOne(id);
        if (flight == null) {
            return null;
        }

        Long campaignId = fetchCampaignId(flight);
        setCampaignPart(flight, campaignService.findFlightPart(campaignId));
        return flight;
    }

    @Override
    @Transactional(readOnly = true)
    public Flight findEager(Long id) {
        Flight flight = find(id);
        if (flight == null) {
            return null;
        }

        forFindEager(flight);
        return flight;
    }

    @Override
    public CreativeIdsProjection findCreativeIds(Long flightId) {
        return flightRepository.findCreativeIdsById(flightId);
    }

    @Override
    public SiteIdsProjection findSiteIds(Long flightId) {
        return flightRepository.findSiteIdsById(flightId);
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosFlightViolationsServiceImpl")
    public Flight create(Flight flight, boolean createInternalLineItem) {
        setDefaultDevicesIfNotSet(flight, flight.getOpportunity().getAccountId());

        Flight persisted = flightRepository.save(flight);
        Long campaignId = updateFlightCampaign(persisted);

        campaignAllocationRepository.save(newAllocation(campaignId, persisted.getOpportunity()));
        if (createInternalLineItem) lineItemService.createInternal(LineItemBuilder.defaultLineItem(persisted));
        return persisted;
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosFlightViolationsServiceImpl")
    public Flight create(Flight flight) {
        return create(flight, true);
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosFlightViolationsServiceImpl")
    public Flight update(Flight flight) {
        return runWithFlightLock(flight.getId(), () -> updateImpl(flight));
    }

    private Flight updateImpl(Flight flight) {
        Flight existing = flightRepository.findOne(flight.getId());

        flight.getOpportunity().setCampaignAllocations(existing.getOpportunity().getCampaignAllocations());
        setDefaultDevicesIfNotSet(flight);
        setIdsForExistingSchedules(flight, existing);

        Long campaignId = updateFlightCampaign(flight);
        Flight persisted = flightRepository.save(flight);
        updateFlightAllocation(persisted, campaignId);

        List<LineItem> lineItems = lineItemService.findByFlightId(flight.getId());
        lineItems.forEach(lineItem -> lineItemService.updateDefaultValues(lineItem));

        return persisted;
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(Long id) {
        runWithFlightLock(id, () -> deleteImpl(id));
    }

    private void deleteImpl(Long id) {
        Flight flight = flightRepository.findOne(id);
        updateFlightCampaignStatus(flight, Status.DELETED);
    }

    @Override
    @Transactional(readOnly = true)
    public void inactivate(Long id) {
        runWithFlightLock(id, () -> inactivateImpl(id));
    }

    private void inactivateImpl(Long id) {
        Flight flight = flightRepository.findOne(id);
        updateFlightCampaignStatus(flight, Status.INACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public void activate(Long id) {
        runWithFlightLock(id, () -> activateImpl(id));
    }

    private void activateImpl(Long id) {
        Flight flight = flightRepository.findOne(id);
        FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flight.getId(), lineItemService);
        if (liInfo.isDefaultLineItemExist()) {
            // We should activate default line item automatically
            lineItemService.activateInternal(liInfo.getDefaultLineItemId());
        }

        updateFlightCampaignStatus(flight, Status.ACTIVE);
    }

    @Override
    @Transactional
    public void linkAdvertisingChannels(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        runWithFlightLock(flightId,
                () -> linkAdvertisingChannelsImpl(flightId, channelIds, linkSpecialChannelFlag));
    }

    public void linkAdvertisingChannelsImpl(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        Flight existing = flightRepository.findOne(flightId);
        List<Long> existingIds = existing.getChannelIds();
        if (existingIds.size() == channelIds.size() &&
                existingIds.containsAll(channelIds) &&
                existing.getSpecialChannelLinked().equals(linkSpecialChannelFlag)) {
            return;
        }

        existing.setSpecialChannelLinked(linkSpecialChannelFlag);
        existing.setChannelIds(channelIds);
        lineItemService.linkAdvertisingChannelsFromFlight(existing);
    }

    @Override
    @Transactional
    public void linkSites(Long flightId, List<Long> siteIds) {
        runWithFlightLock(flightId,
                () -> linkImpl(flightId,
                        siteIds,
                        flight -> flight.getSiteIds(),
                        (flight, ids) -> flight.setSiteIds(ids),
                        (service, flight) -> service.linkSitesFromFlight(flight)));
    }

    @Override
    @Transactional
    public void linkConversions(Long flightId, List<Long> conversionIds) {
        runWithFlightLock(flightId,
                () -> linkImpl(flightId,
                        conversionIds,
                        flight -> flight.getConversionIds(),
                        (flight, ids) -> flight.setConversionIds(ids),
                        (service, flight) -> service.linkConversionsFromFlight(flight)));
    }

    @Override
    @Transactional
    public void linkCreatives(Long flightId, List<Long> creativeIds) {
        runWithFlightLock(flightId,
                () -> linkImpl(flightId,
                        creativeIds,
                        flight -> flight.getCreativeIds(),
                        (flight, ids) -> flight.setCreativeIds(ids),
                        (service, flight) -> service.linkCreativesFromFlight(flight)));
    }

    private void linkImpl(Long flightId, List<Long> ids, FlightBaseLinksGetter getter, FlightBaseLinksSetter setter, LineItemServiceCall service) {
        Flight flight = flightRepository.findOne(flightId);
        List<Long> existingIds = getter.get(flight);
        if (existingIds.size() == ids.size() &&
                existingIds.containsAll(ids)) {
            return;
        }

        setter.set(flight, ids);
        service.call(lineItemService, flight);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public void uploadIoAttachment(MultipartFile file, Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.UPDATE_CAMPAIGN, fetchCampaignId(flight));

        try {
            fileService.uploadToIoRootAsAdmin(file, flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), "");
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public List<String> listAttachments(Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CAMPAIGN, fetchCampaignId(flight));

        try {
            return fileService.listFromIoRootAsAdmin(flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), "");
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public byte[] downloadAttachment(String name, Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CAMPAIGN, fetchCampaignId(flight));

        return fileService.downloadFromIoRootAsAdmin(flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), name);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public void deleteAttachment(String name, Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.UPDATE_CAMPAIGN, fetchCampaignId(flight));

        try {
            fileService.deleteFromIoRootAsAdmin(flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), name);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> fetchCampaignIds(List<Long> flightIds) {
        Iterator<Flight> flightIterator = flightRepository.findAll(flightIds).iterator();
        HashMap<Long, Long> resultMap = new HashMap<>(flightIds.size());
        while (flightIterator.hasNext()) {
            Flight flight = flightIterator.next();
            resultMap.put(flight.getId(), fetchCampaignId(flight));
        }

        return flightIds.stream()
                .map(id -> resultMap.get(id))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateAllocationAndCampaignRelation(Flight flight) {
        runWithFlightLock(flight.getId(), () -> updateAllocationAndCampaignRelationImpl(flight));
    }

    private void updateAllocationAndCampaignRelationImpl(Flight flight) {
        Long campaignId = fetchCampaignId(flight);

        jdbcOperations.execute(String.format("select campaign_util.oncampaignallocationchanged(%d)", campaignId));

        changeTrackerService.saveChange(TableName.CAMPAIGN, campaignId);
    }

    private Flight findForAttachments(Long flightId) {
        Flight flight = flightId == null ? null : flightRepository.findOne(flightId);
        if (flight == null) {
            throw new EntityNotFoundException(flightId);
        }

        return flight;
    }

    private static FlightBaseStat rsToStat(ResultSet rs) throws SQLException {
        FlightBaseStat stat = new FlightBaseStat();

        stat.setId(rs.getLong("flight_id"));
        stat.setName(rs.getString("name"));
        stat.setDisplayStatus(CampaignDisplayStatus.valueOf(rs.getInt("display_status_id")).getMajorStatus());
        stat.setRequests(rs.getLong("requests"));
        stat.setImpressions(rs.getLong("imps"));
        stat.setClicks(rs.getLong("clicks"));
        stat.setCtr(rs.getBigDecimal("ctr"));
        stat.setEcpm(rs.getBigDecimal("ecpm"));
        stat.setTotalCost(rs.getBigDecimal("total_cost"));

        return stat;
    }

    private void updateFlightAllocation(Flight flight, Long campaignId) {
        CampaignAllocation allocation;
        if (!flight.getOpportunity().getCampaignAllocations().isEmpty()) {
            allocation = flight.getOpportunity().getCampaignAllocations().iterator().next();
            BigDecimal newAmount = flight.getOpportunity().getAmount();
            if (allocation.getAmount().compareTo(newAmount) == 0) {
                return;
            }
            allocation.setAmount(newAmount);
            allocation.setStatus(CampaignAllocationStatus.A);
        } else {
            allocation = newAllocation(campaignId, flight.getOpportunity());
        }

        campaignAllocationRepository.save(allocation);
    }

    private Long updateFlightCampaign(Flight flight) {
        Long existingCampaignId = fetchCampaignId(flightRepository.findOne(flight.getId()));
        Campaign existingCampaign = existingCampaignId == null ? null : campaignService.find(existingCampaignId);

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(flight.getOpportunity().getAccountId());
        Campaign result = CampaignBuilder.build(flight,
                authorizationService.getAuthUserInfo().getId(),
                account.getTimeZone(),
                existingCampaign);
        return campaignService.createOrUpdate(result);
    }

    private void updateFlightCampaignStatus(Flight flight, Status status) {
        Long campaignId = fetchCampaignId(flight);
        if (campaignId != null) {
            updateCampaignStatus(campaignId, status);
        }
    }

    private void updateCampaignStatus(Long campaignId, Status status) {
        Campaign campaign = campaignService.find(campaignId);
        campaign.setStatus(status);
        campaignService.createOrUpdate(campaign);
    }

    private CampaignAllocation newAllocation(Long campaignId, Opportunity opportunity) {
        CampaignAllocation allocation = new CampaignAllocation();
        allocation.setCampaignId(campaignId);
        allocation.setOpportunity(opportunity);
        allocation.setAmount(opportunity.getAmount());
        allocation.setOrder(1L);
        allocation.setStatus(CampaignAllocationStatus.A);

        opportunity.getCampaignAllocations().add(allocation);

        return allocation;
    }

    private static void setCampaignPart(Flight flight, CampaignFlightPart campaignPart) {
        if (campaignPart == null) {
            logger.log(Level.WARNING, "Flight has no Campaign, flight id = " + flight.getId());
            return;
        }

        flight.setDisplayStatus(campaignPart.getDisplayStatus());
    }

    private interface LineItemServiceCall {
        void call(LineItemServiceInternal service, Flight flight);
    }
}
