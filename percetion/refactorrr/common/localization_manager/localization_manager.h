//
// Copyright 2021 Inceptio Technology. All Rights Reserved.
//
#ifndef REFACTORRR_COMMON_LOCALIZATION_MANAGER_LOCALIZATION_MANAGER_H_
#define REFACTORRR_COMMON_LOCALIZATION_MANAGER_LOCALIZATION_MANAGER_H_

#include <mutex>

#include "refactorrr/common/localization_manager/relative_motion_result.h"
#include "refactorrr/common/math/polation_queue.h"
#include "refactorrr/common/math/pose.h"
#include "refactorrr/lib/inceptio/allspark/base/base.h"
#include "refactorrr/lib/inceptio/megatron/localization/localization_result.h"
#include "refactorrr/lib/inceptio/megatron/localization/rel_motion_history_querier.h"
#include "refactorrr/lib/inceptio/megatron/localization/tract_loc_result.h"
#include "refactorrr/lib/inceptio/optimus/interfaces/localization/localization_result.h"

namespace refactorrr {
namespace common {

///
/// @brief Localization manager provides localization service for all classes by
/// making it a singleton. It accepts subscribed localization (or relative
/// motion) topics as input, and outputs the relative motion result at a certain
/// timestamp or between two timestamps
class LocalizationManager {
 public:
  ~LocalizationManager() = default;

  static LocalizationManager& GetInstance() {
    static LocalizationManager loc_manager;
    return loc_manager;
  }

  ///
  /// @brief Put a relative motion result into relative motion querier
  ///
  /// @param timestamp_ns timestamp in nanosec
  /// @param rel_mot_rslt relative motion result
  /// @return bool whether put is successful
  bool Put(const int64_t timestamp_ns,
           const lib::localization::TractChassRelMotionResult& rel_mot_rslt);

  ///
  /// @brief Put a relative motion result into relative motion querier
  ///
  /// @param rel_mot_rslt relative motion result
  /// @return bool whether put is successful
  bool Put(const lib::localization::TractChassRelMotionResult& rel_mot_rslt);

  ///
  /// @brief Put a relative motion result into relative motion querier
  ///
  /// @param timestamp_ns timestamp in nanosec
  /// @param rel_mot_rslt_idl relative motion result idl
  /// @return bool whether put is successful
  bool Put(const int64_t timestamp_ns,
           const lib::interfaces::localization::TractChassRelMotionResult&
               rel_mot_rslt_idl);

  ///
  /// @brief Put a relative motion result into relative motion querier
  ///
  /// @param rel_mot_rslt_idl relative motion result idl
  /// @return bool whether put is successful
  bool Put(const lib::interfaces::localization::TractChassRelMotionResult&
               rel_mot_rslt_idl);

  ///
  /// @brief Put a relative motion result into relative motion querier
  ///
  /// @param timestamp_ns timestamp in nanosec
  /// @param rel_mot_rslt_idl relative motion result idl
  /// @return bool whether put is successful
  bool Put(const int64_t timestamp_ns,
           const lib::interfaces::localization::RelativeMotionResult&
               rel_mot_rslt_idl);

  ///
  /// @brief Put a relative motion result into relative motion querier
  ///
  /// @param rel_mot_rslt_idl relative motion result idl
  /// @return bool whether put is successful
  bool Put(const lib::interfaces::localization::RelativeMotionResult&
               rel_mot_rslt_idl);

  ///
  /// @brief Get the relative motion result at a timestamp. The transformation
  /// will be unavailable in the output due to the missing reference timestamp
  ///
  /// @param timestamp_ns the query timestamp
  /// @param rel_mot_rslt relative motion result without transformation at query
  /// timestamp
  /// @return bool whether get is successful
  bool Get(const int64_t timestamp_ns,
           RelativeMotionResult* rel_mot_rslt) const;

  ///
  /// @brief Get the relative motion result between query timestamp and
  /// reference timestamp
  ///
  /// @param timestamp_ns the query timestamp
  /// @param ref_timestamp_ns the reference timestamp
  /// @param rel_mot_rslt relative motion result between two timestamps
  /// @return bool whether get is successful
  bool Get(const int64_t timestamp_ns, const int64_t ref_timestamp_ns,
           RelativeMotionResult* rel_mot_rslt) const;

  ///
  /// @brief Get latest localization timestamp in ns
  ///
  /// @return int64_t latest localization timestamp
  int64_t GetLatestTimestampNs() const;

 private:
  LocalizationManager();

  DELETE_COPY_AND_MOVE(LocalizationManager);

 private:
  std::mutex lock_;

  int64_t latest_timestamp_ns_ = 0;

  lib::localization::TractChassRelMotRsltHistQuerier querier_;
};

}  // namespace common
}  // namespace refactorrr

#endif  // REFACTORRR_COMMON_LOCALIZATION_MANAGER_LOCALIZATION_MANAGER_H_
