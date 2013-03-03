Fight = do ->
  elements = {
    latenessRatio:
      swe: -> $("#lateness-ratio dd.swe")
      fin: -> $("#lateness-ratio dd.fin")

    averageLateness:
      swe: -> $("#average-lateness dd.swe")
      fin: -> $("#average-lateness dd.fin")

    results:
      swe: -> $("#results dd.swe")
      fin: -> $("#results dd.fin")
  }

  analyse = {
    vr:
      latenessRatio: (trains) ->
        [late, total] = [0, 0]
        
        for train in trains
          total += 1
          late += 1 if train.lateness > 0
        
        [late, total]
      
      averageLateness: (trains) ->
        [lateness, instances] = [0, 0]
        
        for train in trains
           if train.lateness > 0
             lateness += train.lateness
             instances += 1

        return 0 if instances is 0
        lateness / instances 
  }

  start = ->

    Bacon.fromPromise($.getJSON("vr/trains"))
      .flatMap((data) ->
        Bacon.fromArray(data.trains).flatMap((train) ->
          Bacon.fromPromise($.getJSON("vr/trains/#{train.guid}"))
        )
      ).scan([], (trains, train) -> [trains..., train])
      .onValue((v) -> console.log v)

    Bacon.fromPromise($.getJSON("sj/trains"))
      .onValue((v) -> console.log v)

    setContent = (element) -> (content) -> element.text(content)

    randomIntegerStream = (from, to) -> Bacon.interval(100).map(-> Math.floor((to - from) * Math.random()) + from)

    randomIntegerStream(0,4).onValue(setContent(elements.results.swe()))
    randomIntegerStream(0,4).onValue(setContent(elements.results.fin()))

    randomIntegerStream(0,100).onValue(setContent(elements.latenessRatio.swe()))
    randomIntegerStream(0,100).onValue(setContent(elements.latenessRatio.fin()))

    randomIntegerStream(0,10).onValue(setContent(elements.averageLateness.swe()))
    randomIntegerStream(0,10).onValue(setContent(elements.averageLateness.fin()))

  { start }

$(document).ready Fight.start