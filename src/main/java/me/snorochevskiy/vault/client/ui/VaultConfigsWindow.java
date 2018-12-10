package me.snorochevskiy.vault.client.ui;

import me.snorochevskiy.vault.client.lib.VaultConfigs;
import me.snorochevskiy.vault.client.lib.VaultConfigsStorage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class VaultConfigsWindow extends JDialog {

    private JTextField vaultUrlText = new JTextField("https://vault.ololo.com:8200", 128);
    private JTextField rootSecret = new JTextField("<root secret>", 128);
    private JTextField githubTokenText = new JTextField("<set github token>", 128);
    private JTextField organizationText = new JTextField("<set github organization>", 128);

    private JButton applyButton = new JButton("Apply");

    private Consumer<VaultConfigs> onApply;

    public VaultConfigsWindow(JFrame owner, Consumer<VaultConfigs> onApply) {
        super(owner, true);

        this.onApply = onApply;

        this.setSize(400, 300);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);

        applyButton.addActionListener(a-> {
            // TODO: validate
            VaultConfigs vc = new VaultConfigs();
            vc.setVaultUrl(vaultUrlText.getText());
            vc.setRooSecret(rootSecret.getText());
            vc.setGitToken(githubTokenText.getText());
            vc.setOrganization(organizationText.getText());
            onApply.accept(vc);
        });

        Container contentPane = this.getContentPane();

        contentPane.setLayout(new MigLayout("wrap 2"));

        contentPane.add(new JLabel("Config file"));
        contentPane.add(new JLabel(VaultConfigsStorage.CONFIGS_FILE));

        contentPane.add(new JLabel("Vault URL"));
        contentPane.add(vaultUrlText);

        contentPane.add(new JLabel("Root secret"));
        contentPane.add(rootSecret);

        {
            JPanel githubConfigPanel = new JPanel();
            githubConfigPanel.setLayout(new MigLayout("wrap 2"));
            githubConfigPanel.setBorder(BorderFactory.createTitledBorder("Github credentials:"));

            githubConfigPanel.add(new JLabel("Github token"));
            githubConfigPanel.add(githubTokenText);

            githubConfigPanel.add(new JLabel("Organization"));
            githubConfigPanel.add(organizationText);

            contentPane.add(githubConfigPanel, "span 2");
        }

        contentPane.add(applyButton);
    }

    public void setVaultConfigs(VaultConfigs vc) {
        vaultUrlText.setText(vc.getVaultUrl());
        rootSecret.setText(vc.getRooSecret());
        githubTokenText.setText(vc.getGitToken());
        organizationText.setText(vc.getOrganization());
    }
}
