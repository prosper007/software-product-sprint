// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if(request.getAttendees().isEmpty()){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
      return Arrays.asList();
    }
    if(events.isEmpty()){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    Collection<String> listOfAttendees = request.getAttendees();
    Map<String, Collection<TimeRange>> mapOfAttendeeAvailableTimes = new HashMap<>();
    
    for(String attendee : listOfAttendees){
      System.out.println(attendee);
      Collection<TimeRange> availableTimesForAttendee = getAvailableTimesForAttendee(attendee, events);
      mapOfAttendeeAvailableTimes.put(attendee, availableTimesForAttendee);
    }
    
    // return findIntersectionOfAvailableTimes(mapOfAttendeeAvailableTimes);

    throw new UnsupportedOperationException("TODO: Implement this method.");
  }

  public Collection<TimeRange> getAvailableTimesForAttendee(String attendee, Collection<Event> events){
    List<TimeRange> unavailableTimes = new ArrayList<>();
    for(Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      if(eventAttendees.contains(attendee)) {
        unavailableTimes.add(event.getWhen());
      }
    }
    int startTime = TimeRange.START_OF_DAY;
    Collections.sort(unavailableTimes, TimeRange.ORDER_BY_START);
    Collection<TimeRange> availableTimes = new ArrayList<>();
    
    for(TimeRange unavailableTime: unavailableTimes) {
      if(startTime < unavailableTime.start()) {
        availableTimes.add(TimeRange.fromStartEnd(startTime, unavailableTime.start(), false));
      }
      if(unavailableTime.end() > startTime){
        startTime = unavailableTime.end();
      }
    }
    if(startTime != TimeRange.END_OF_DAY+1){
      availableTimes.add(TimeRange.fromStartEnd(startTime, TimeRange.END_OF_DAY, true));
    }
    return availableTimes;
  }
}
