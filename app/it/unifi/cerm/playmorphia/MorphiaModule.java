package it.unifi.cerm.playmorphia;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by morelli on 12/20/16.
 */
public class MorphiaModule extends Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(bind(PlayMorphia.class).toSelf());
    }
}
