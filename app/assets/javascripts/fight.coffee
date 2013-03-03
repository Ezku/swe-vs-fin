Fight = do ->

  metrics = do ->
    averageLateness = (trains) ->
      [lateness, instances] = [0, 0]
      
      for train in trains
         if train.lateness > 0
           lateness += ~~train.lateness
           instances += 1

      [lateness, instances]

    treshold = (lateness) -> (trains) ->
      [late, instances] = [0, 0]
      
      for train in trains
        instances += 1
        late += 1 if ~~train.lateness > lateness
      
      [late, instances]

    return (trains) ->
      averageLateness: averageLateness trains
      latenessRatio: treshold(0) trains
      tooLate: treshold(180) trains

  start = ->

    busFor = (element) ->
      bus = (new Bacon.Bus)
      bus.map(([dividend, divisor]) -> "#{dividend}/#{divisor}")
         .assign(element, "text")
      bus

    elements = (side) -> (section) -> $("##{section} dd.#{side}")
    resultBuses = (side) ->
      element = elements(side)

      averageLateness: busFor(element("average-lateness"))
      latenessRatio: busFor(element("lateness-ratio"))
      tooLate: busFor(element("too-late"))

    results =
      fin: resultBuses("fin")
      swe: resultBuses("swe")

    for source, uri of { fin: "vr/trains", swe: "sj/trains" }
      do (source) ->
        Bacon
          .fromPromise($.getJSON(uri))
          .map((data) -> data.trains)
          .map(metrics)
          .onValue((metrics) ->
            for name, value of metrics
              results[source][name].push value
              results[source][name].end()
          )


    setContent = (element) -> (content) -> element.text(content)

    ###
    randomIntegerStream = (from, to) -> Bacon.interval(100).map(-> Math.floor((to - from) * Math.random()) + from)

    randomIntegerStream(0,4).onValue(setContent(elements.results.swe()))
    randomIntegerStream(0,4).onValue(setContent(elements.results.fin()))

    randomIntegerStream(0,100).onValue(setContent(elements.latenessRatio.swe()))
    randomIntegerStream(0,100).onValue(setContent(elements.latenessRatio.fin()))

    randomIntegerStream(0,10).onValue(setContent(elements.averageLateness.swe()))
    randomIntegerStream(0,10).onValue(setContent(elements.averageLateness.fin()))
    ###

  { start }

$(document).ready Fight.start