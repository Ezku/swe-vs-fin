Fight = do ->

  metrics = do ->
    treshold = (lateness) -> (trains) ->
      [late, instances] = [0, 0]
      
      for train in trains
        instances += 1
        late += 1 if ~~train.lateness > lateness
      
      [late, instances]

    medianLateness = (trains) ->
      latenesses = (~~train.lateness for train in trains when train.lateness > 0)
      latenesses.sort (left, right) -> right - left
      middle = Math.floor (latenesses.length / 2)
      if latenesses.length % 2 is 0
        Math.round ((latenesses[middle-1] + latenesses[middle]) / 2)
      else
        latenesses[middle]

    return (trains) ->
      medianLateness: medianLateness trains
      latenessRatio: treshold(0) trains
      tooLate: treshold(180) trains


  start = ->
    busFor = (element) -> (treatment) ->
      bus = (new Bacon.Bus)
      bus.map(treatment)
         .assign(element, "text")
      bus

    elements = (side) -> (section) -> $("##{section} dd.#{side}")
    resultBuses = (side) ->
      element = elements(side)

      medianLateness: busFor(element("median-lateness"))((median) -> "#{Math.round(median/60)} min")
      latenessRatio: busFor(element("lateness-ratio"))(([dividend, divisor]) -> "#{dividend} out of #{divisor}")
      tooLate: busFor(element("too-late"))(([dividend, divisor]) -> "#{dividend} out of #{divisor}")

    results =
      fin: resultBuses("fin")
      swe: resultBuses("swe")

    streams = for source, uri of { fin: "vr/trains", swe: "sj/trains" }
      do (source) ->
        Bacon
          .fromPromise($.getJSON(uri))
          .map((data) -> metrics data.trains)
          .map((metrics) -> { source, metrics })

    Bacon
      .mergeAll(streams)
      .doAction(({source, metrics}) ->
        for name, value of metrics
          results[source][name].push value
          results[source][name].end()
      ).scan(0, (done) -> done + 1)
      .filter((done) -> done is 2)
      .onValue( ->
        $("#intro p").text("Three servings of artisanally crafted data at your disposal.")
        $("#results h2").text("Wow, that took a while.")
      )

  { start }

$(document).ready Fight.start