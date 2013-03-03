# Sweden vs Finland - public transport edition

This application attempts to depict how well trains in Sweden and Finland manage to keep their schedules in the spirit of friendly competition. Lateness data is based on open APIs from [sj.se](http://sj.se) through [trafiklab.se](http://trafiklab.se) for Sweden and [vr.fi](http://vr.fi) for Finland.

## Deployment

- Acquire permission to use VR's Open Data API, no key required
- Register for trafiklab and acquire an API key for the Trafikverket Trainexport API, then place the key in `conf/application.conf` under the Trainexport entry
- Install Play framework and start the application with `play run` on the command line. You can access the application at `localhost:9000`.

## Attributions

- Background image: [Subtle patterns](http://subtlepatterns.com/maze-black/)
- Font face: [Google web fonts](http://www.google.com/webfonts/specimen/Lato)
- Color palette: [Colourlovers](http://www.colourlovers.com/palette/2725463/Bright_Day-IFRC)
- Typography inspiration: [Typeplate](http://typeplate.com)
