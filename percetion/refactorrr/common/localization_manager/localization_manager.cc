//
// Copyright 2021 Inceptio Technology. All Rights Reserved.
//

#include "refactorrr/common/localization_manager/localization_manager.h"

#include <mutex>

namespace refactorrr {
namespace common {
namespace {
namespace pq = math::polation_queue;
using RelativeMotionResultIdl =
    lib::interfaces::localization::RelativeMotionResult;
using TractChassRelMotionResultIdl =
    lib::interfaces::localization::TractChassRelMotionResult;
using TractChassRelMotionResult = lib::localization::TractChassRelMotionResult;
using TractChassGlobLocResult = lib::localization::TractChassGlobLocResult;

/// @brief Threshold for relative motion result query interpolation, using value
/// recommended by localization team
constexpr int64_t kRelMotRsltQuerierInterpolationTimeNs = IRS_S_TO_NS(2);
/// @brief Threshold for relative motion result query extrapolation, using value
/// recommended by localization team
constexpr int64_t kRelMotRsltQuerierExtrapolationTimeNs = IRS_MS_TO_NS(80);
}  // namespace

LocalizationManager::LocalizationManager()
    : querier_(kRelMotRsltQuerierInterpolationTimeNs,
               kRelMotRsltQuerierExtrapolationTimeNs) {}

bool LocalizationManager::Put(const int64_t timestamp_ns,
                              const TractChassRelMotionResult& rel_mot_rslt) {
  std::lock_guard<std::mutex> lock(lock_);

  if (!pq::IsPQStatusOk(querier_.Put(timestamp_ns, rel_mot_rslt))) {
    LOG_WARN("LocalizationManager",
             "[Put] Failed to put localization result at %ld", timestamp_ns);
    return false;
  }
  LOG_INFO("LocalizationManager", "[Put] Put localization result at %ld",
           timestamp_ns);

  if (timestamp_ns > latest_timestamp_ns_) {
    latest_timestamp_ns_ = timestamp_ns;
  }
  return true;
}

bool LocalizationManager::Put(const TractChassRelMotionResult& rel_mot_rslt) {
  return Put(rel_mot_rslt.unix_publish_ts_ns, rel_mot_rslt);
}

bool LocalizationManager::Put(
    const int64_t timestamp_ns,
    const TractChassRelMotionResultIdl& rel_mot_rslt_idl) {
  return Put(timestamp_ns,
             lib::localization::ToTractChassRelMotionResult(rel_mot_rslt_idl));
}

bool LocalizationManager::Put(
    const TractChassRelMotionResultIdl& rel_mot_rslt_idl) {
  return Put(rel_mot_rslt_idl.unix_publish_ts_ns.nanosec, rel_mot_rslt_idl);
}

bool LocalizationManager::Put(const int64_t timestamp_ns,
                              const RelativeMotionResultIdl& rel_mot_rslt_idl) {
  return Put(timestamp_ns, rel_mot_rslt_idl.tract_chass_cont_rel_motion_result);
}

bool LocalizationManager::Put(const RelativeMotionResultIdl& rel_mot_rslt_idl) {
  return Put(rel_mot_rslt_idl.unix_publish_ts_ns.nanosec, rel_mot_rslt_idl);
}

bool LocalizationManager::Get(const int64_t timestamp_ns,
                              RelativeMotionResult* rel_mot_rslt) const {
  if (nullptr == rel_mot_rslt) {
    return false;
  }

  TractChassGlobLocResult loc_rslt;
  if (!pq::IsPQStatusOk(querier_.Get(timestamp_ns, &loc_rslt))) {
    LOG_WARN("LocalizationManager",
             "[Get] Failed to get localization result at %ld", timestamp_ns);
    return false;
  }
  loc_rslt.unix_publish_ts_ns = timestamp_ns;
  *rel_mot_rslt = RelativeMotionResult(loc_rslt);
  return true;
}

bool LocalizationManager::Get(const int64_t timestamp_ns,
                              const int64_t ref_timestamp_ns,
                              RelativeMotionResult* rel_mot_rslt) const {
  if (nullptr == rel_mot_rslt) {
    return false;
  }

  TractChassGlobLocResult loc_rslt, ref_loc_rslt;
  if (!pq::IsPQStatusOk(querier_.Get(timestamp_ns, &loc_rslt)) ||
      !pq::IsPQStatusOk(querier_.Get(ref_timestamp_ns, &ref_loc_rslt))) {
    LOG_WARN("LocalizationManager",
             "[Get] Failed to get localization result from %ld to %ld",
             ref_timestamp_ns, timestamp_ns);
    return false;
  }
  loc_rslt.unix_publish_ts_ns = timestamp_ns;
  ref_loc_rslt.unix_publish_ts_ns = ref_timestamp_ns;
  *rel_mot_rslt = RelativeMotionResult(loc_rslt, ref_loc_rslt);
  return true;
}

int64_t LocalizationManager::GetLatestTimestampNs() const {
  return latest_timestamp_ns_;
}

}  // namespace common
}  // namespace refactorrr
