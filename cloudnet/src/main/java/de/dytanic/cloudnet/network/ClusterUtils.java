package de.dytanic.cloudnet.network;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.permission.DefaultJsonFilePermissionManagement;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.network.packet.*;
import de.dytanic.cloudnet.permission.DefaultDatabasePermissionManagement;
import de.dytanic.cloudnet.template.ITemplateStorage;
import de.dytanic.cloudnet.template.LocalTemplateStorage;

import java.util.Collection;

public class ClusterUtils {

    private ClusterUtils() {
        throw new UnsupportedOperationException();
    }

    public static void sendSetupInformationPackets(INetworkChannel channel) {
        sendSetupInformationPackets(channel, false);
    }

    public static void sendSetupInformationPackets(INetworkChannel channel, boolean secondNodeConnection) {
        channel.sendPacket(new PacketServerSetGlobalServiceInfoList(CloudNet.getInstance().getCloudServiceManager().getGlobalServiceInfoSnapshots().values()));
        if (!secondNodeConnection) {
            CloudNet.getInstance().publishH2DatabaseDataToCluster(channel);

            if (CloudNet.getInstance().getPermissionManagement() instanceof DefaultJsonFilePermissionManagement) {
                Collection<IPermissionUser> users = CloudNet.getInstance().getPermissionManagement().getUsers();
                channel.sendPacket(new PacketServerSetPermissionData(users, NetworkUpdateType.ADD));
            }
        }
    }

}
