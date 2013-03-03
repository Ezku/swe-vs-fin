Fight = do ->
  elements = {
    latenessRatio:
      swe: -> $("#lateness-ratio dd.swe")
      fin: -> $("#lateness-ratio dd.fin")

    averageLateness:
      swe: -> $("#average-lateness dd.swe")
      fin: -> $("#average-lateness dd.fin")

    tooLate:
      swe: -> $("#too-late dd.swe")
      fin: -> $("#too-late dd.fin")

    results:
      swe: -> $("#results dd.swe")
      fin: -> $("#results dd.fin")
  }

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

    results =
      vr:
        averageLateness: busFor(elements.averageLateness.fin())
        latenessRatio: busFor(elements.latenessRatio.fin())
        tooLate: busFor(elements.tooLate.fin())

    vr = Bacon
      .fromPromise($.getJSON("vr/trains"))
      .map((data) -> data.trains)
      .map(metrics)
      .onValue((metrics) ->
        for name, value of metrics
          results.vr[name].push value
          results.vr[name].end()
      )

    sj = Bacon.fromPromise($.getJSON("sj/trains"))
      .map((data) -> data.trains)
      .map(metrics)
      .onValue((v) -> console.log sj: v)


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