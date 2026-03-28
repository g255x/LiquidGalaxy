<script lang="ts">
    import Modal from "../common/modal/Modal.svelte";
    import IconTextInput from "../common/setting/IconTextInput.svelte";
    import SwitchSetting from "../common/setting/SwitchSetting.svelte";
    import ButtonSetting from "../common/setting/ButtonSetting.svelte";
    import {editProxy as editProxyRest} from "../../../integration/rest";
    import {listen} from "../../../integration/ws";
    import SingleSelect from "../common/setting/select/SingleSelect.svelte";

    export let visible: boolean;
    export let id: number;
    export let proxyType: string;
    export let host: string;
    export let port: number;
    export let username: string;
    export let password: string;
    export let requiresAuthentication: boolean;
    export let forwardAuthentication: boolean;

    let hostPort = `${host}:${port}`;
    let loading = false;

    $: disabled = validateInput(requiresAuthentication, hostPort, username, password);
    $: {
        if (!requiresAuthentication) {
            username = "";
            password = "";
        }
    }

    function validateInput(requiresAuthentication: boolean, hostPort: string, username: string, password: string): boolean {
        let valid = /.+:[0-9]+/.test(hostPort);

        if (requiresAuthentication) {
            valid &&= username.length > 0 && password.length > 0;
        }

        return !valid;
    }

    async function editProxy() {
        if (disabled) {
            return;
        }

        const [host, port] = hostPort.split(":");

        loading = true;
        await editProxyRest(id, host, parseInt(port), username, password, proxyType, forwardAuthentication);
    }

    listen("proxyCheckResult", () => {
        visible = false;
        loading = false;
    });
</script>

<Modal title="编辑代理" bind:visible={visible}>
    <IconTextInput title="主机:端口" icon="server" pattern=".+:[0-9]+" bind:value={hostPort}/>
    <SingleSelect title="代理类型" options={["HTTP", "SOCKS5"]} bind:value={proxyType}/>
    <SwitchSetting title="需要认证" bind:value={requiresAuthentication}/>
    {#if requiresAuthentication}
        <IconTextInput title="用户名" icon="user" bind:value={username}/>
        <IconTextInput title="密码" icon="lock" type="password" bind:value={password}/>
    {/if}
    <SwitchSetting title="转发 Microsoft 认证" bind:value={forwardAuthentication}/>
    <ButtonSetting title="编辑代理" {disabled} on:click={editProxy} listenForEnter={true} {loading}/>
</Modal>
