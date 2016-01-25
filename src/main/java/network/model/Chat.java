package network.model;

import java.util.*;

/**
 * Created by brian on 1/24/16.
 */
public class Chat {
    public Map<String,ChatPartner> partners = new HashMap<>();
    public Map<String,Message> messages = new HashMap<>();
}
